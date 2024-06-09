package com.mementee.api.controller.chat;

import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.dto.chatDTO.ChatRoomUpdateDTO;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.service.*;
import com.mementee.exception.notFound.FileNotFound;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@Tag(name = "실시간 채팅 기능")
public class ChatController {

    private final ChatService chatService;
    private final MemberService memberService;
    private final FcmService fcmService;
    private final FileService fileService;
    private final SimpMessagingTemplate websocketPublisher;

    @MessageMapping("/hello")
    public void sendMessage(ChatMessageDTO messageDTO) {
        // If both users are in the chat room, set the readCount to 2.
        chatService.setMessageReadCount(messageDTO);

        //DB에 저장
        chatService.saveMessage(messageDTO);

        // webSocket에 보내기
        websocketPublisher.convertAndSend("/sub/chats/" + messageDTO.getChatRoomId(), messageDTO);

        extracted(messageDTO);
        //FCM 알림
        FcmDTO fcmDTO = fcmService.createChatFcmDTO(messageDTO);
        fcmService.sendMessageTo(fcmDTO);
    }

    private void extracted(ChatMessageDTO messageDTO) {
        Long senderId = messageDTO.getSenderId();
        Long chatRoomId = messageDTO.getChatRoomId();

        Long receiverId = chatService.findOtherMemberId(chatRoomId, senderId);

        // 메시지를 수신 하는 멤버의 unreadMessageCount를 호출
        int unreadCount = chatService.getUnreadMessageCount(messageDTO.getChatRoomId(), receiverId);

        // WebSocket을 통해 클라이언트에 전송
        ChatRoomUpdateDTO updateDTO = new ChatRoomUpdateDTO(chatRoomId, receiverId, unreadCount);
        websocketPublisher.convertAndSend("/sub/unreadCount/" + chatRoomId, updateDTO);
    }

    @Operation(description = "파일 전송 처리")
    @PostMapping("/sendFile")
    public CommonApiResponse<ChatMessageDTO> sendFileInChatRoom(@RequestHeader("Authorization") String authorizationHeader, @RequestPart("file") MultipartFile file, @RequestParam Long chatRoomId) {
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        ChatMessageDTO messageDTO = new ChatMessageDTO(
                fileService.getFileType(file.getContentType()),
                chatService.saveMultipartFile(file),
                null,
                loginMember.getName(),
                loginMember.getId(),
                chatRoomId,
                1,
                LocalDateTime.now());

        // If both users are in the chat room, set the readCount to 2.
        chatService.setMessageReadCount(messageDTO);

        // If file is not uploaded, return BAD_REQUEST error.
        if (file.isEmpty()) throw new FileNotFound();

        // If a file that has supported contentType is uploaded, save the file in S3 and return the URL.
        chatService.saveMessage(messageDTO);
        return CommonApiResponse.createSuccess(messageDTO);
    }


    @Operation(summary = "채팅방 ID로 모든 채팅 메시지 조회")
    @GetMapping("/messages/{chatRoomId}")
    public CommonApiResponse<Slice<ChatMessageDTO>> findAllMessagesByChatRoom(@RequestParam int page, @RequestParam int size,
                                                                              @PathVariable Long chatRoomId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차순(최신순)
        Slice<ChatMessage> allMessages = chatService.findAllMessagesByChatRoomId(chatRoomId, pageable);

        return CommonApiResponse.createSuccess(allMessages.map(message -> new ChatMessageDTO(
                message.getFileType(),
                message.getFileURL(),
                message.getContent(),
                message.getSender().getName(),
                message.getSender().getId(),
                message.getChatRoom().getId(),
                1,
                message.getLocalDateTime()
        )));
    }

    @Operation(summary = "상대방 ID로 해당 채팅방 조회. 상대방 프로필을 조회하고 메시지를 보낼 때, 둘 사이에 채팅방이 존재하는지 확인. 존재 하지 않으면 null 반환")
    @GetMapping("/chatRoom")
    public CommonApiResponse<?> findChatRoomByReceiverId(@RequestParam Long receiverId, @RequestHeader("Authorization") String authorizationHeader) {
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        Member receiver = memberService.findMemberById(receiverId);

        ChatRoom chatRoom = chatService.findChatRoomBySenderAndReceiver(loginMember, receiver);

        ChatRoomDTO chatRoomDTO = new ChatRoomDTO(chatRoom.getId(), receiverId, receiver.getName());
        return CommonApiResponse.createSuccess(chatRoomDTO);
    }

    @Operation(summary = "특정 멤버가 속한 채팅방 모두 조회")
    @GetMapping("/chatRooms")
    public CommonApiResponse<List<ChatRoomDTO>> findAllChatRoomsByMemberId(@RequestHeader("Authorization") String authorizationHeader) {
        Member member = memberService.findMemberByToken(authorizationHeader);
        List<ChatRoom> allChatRooms = chatService.findAllChatRoomByMemberId(member.getId());

        List<ChatRoomDTO> chatRoomDTOs = allChatRooms.stream()
                .map(chatRoom -> chatService.createChatRoomDTO(member.getId(), chatRoom))
                .collect(Collectors.toList());

        return CommonApiResponse.createSuccess(chatRoomDTOs);
    }
}