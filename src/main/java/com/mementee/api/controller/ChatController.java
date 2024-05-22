package com.mementee.api.controller;

import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.service.*;
import com.mementee.config.chat.RedisPublisher;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final SimpMessagingTemplate websocketPublisher;
    private final RedisPublisher redisPublisher;
    private final FCMNotificationService fcmNotificationService;
    private final FileService fileService;

    @MessageMapping("/hello")
    public void sendMessage(ChatMessageDTO messageDTO){
        // redis에 publish
//        redisPublisher.publish(ChannelTopic.of("chatRoom" + messageDTO.getChatRoomId()), messageDTO);

        // webSocket에 보내기
        websocketPublisher.convertAndSend("/sub/chats/" + messageDTO.getChatRoomId(), messageDTO);

        //DB에 저장
        chatService.saveMessage(messageDTO);

        //FCM 알림
        FcmDTO fcmDTO = fcmNotificationService.createChatFcmDTO(messageDTO);
        fcmNotificationService.sendMessageTo(fcmDTO);
    }

    @Operation(description = "파일 전송 처리")
    @PostMapping(value = "/sendFile" ,
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ChatMessageDTO> sendFileInChatRoom(@RequestHeader("Authorization") String authorizationHeader,
                                                             @RequestPart("file") MultipartFile file, @RequestParam Long chatRoomId) {
        // If file is not uploaded, return BAD_REQUEST error.
        if (file.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);

        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        ChatMessageDTO messageDTO = ChatMessageDTO.createFileChatMessageDTO(fileService.getFileType(file.getContentType()), chatService.saveMultipartFile(file),
                                    loginMember, chatRoomId);

        //FCM 알림
        FcmDTO fcmDTO = fcmNotificationService.createChatFcmDTO(messageDTO);
        fcmNotificationService.sendMessageTo(fcmDTO);

        // If a file that has supported contentType is uploaded, save the file in S3 and return the URL.
        log.info("messageDTO={}", messageDTO);
        chatService.saveMessage(messageDTO);
        return ResponseEntity.status(HttpStatus.OK).body(messageDTO);
    }


    @Operation(description = "채팅방 ID로 모든 채팅 메시지 조회")
    @GetMapping("/messages/{chatRoomId}")
    public ResponseEntity<Slice<ChatMessageDTO>> findAllMessagesByChatRoom(@RequestParam int page, @RequestParam int size,
                                                                           @PathVariable Long chatRoomId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차순(최신순)
        Slice<ChatMessage> allMessages = chatService.findAllMessagesByChatRoomId(chatRoomId, pageable);
        return ResponseEntity.ok(ChatMessageDTO.creatChatMessageDTO(allMessages));
    }

    @Operation(description = "상대방 ID로 해당 채팅방 조회. 상대방 프로필을 조회하고 메시지를 보낼 때, 둘 사이에 채팅방이 존재하는지 확인")
    @GetMapping("/chatRoom")
    public ResponseEntity<ChatRoomDTO> findChatRoomByReceiverId(@RequestParam Long receiverId, @RequestHeader("Authorization") String authorizationHeader) {
            Member loginMember = memberService.findMemberByToken(authorizationHeader);
            Member receiver = memberService.findMemberById(receiverId);
            ChatRoom chatRoom = chatService.findChatRoomBySenderAndReceiver(loginMember, receiver);
            ChatRoomDTO chatRoomDTO = new ChatRoomDTO(chatRoom.getId(), receiverId, receiver.getName());
            return ResponseEntity.ok(chatRoomDTO);
    }

    @Operation(description = "내가 속한 채팅방 모두 조회")
    @GetMapping("/chatRooms")
    public ResponseEntity<List<ChatRoomDTO>> findAllChatRoomsByMemberId(@RequestHeader String authorizationHeader) {
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        List<ChatRoom> allChatRooms = chatService.findAllChatRoomByMember(loginMember);
        List<ChatRoomDTO> chatRoomDTOs = allChatRooms.stream()
                .map(chatRoom -> chatService.createChatRoomDTO(loginMember, chatRoom))
                .collect(Collectors.toList());
        return ResponseEntity.ok(chatRoomDTOs);
    }
}