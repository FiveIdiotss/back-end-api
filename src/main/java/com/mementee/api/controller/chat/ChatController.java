package com.mementee.api.controller.chat;

import com.mementee.api.domain.enumtype.ExtendState;
import com.mementee.api.domain.enumtype.DecisionStatus;
import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.dto.chatDTO.ChatUpdateDTO;
import com.mementee.api.dto.chatDTO.LatestMessageDTO;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.service.*;
import com.mementee.exception.ForbiddenException;
import com.mementee.exception.conflict.ExtendRequestConflictException;
import com.mementee.exception.conflict.ExtendResponseConflictException;
import com.mementee.exception.notFound.FileNotFound;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

import static com.mementee.api.dto.chatDTO.LatestMessageDTO.createLatestMessageDTO;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@Tag(name = "실시간 채팅 기능")
public class ChatController {

    private final ChatService chatService;
    private final MatchingService matchingService;
    private final MemberService memberService;
    private final FcmService fcmService;
    private final FileService fileService;
    private final SimpMessagingTemplate websocketPublisher;

    @MessageMapping("/hello")
    public void sendMessage(ChatMessageDTO messageDTO) {
        Long senderId = messageDTO.getSenderId();
        Long chatRoomId = messageDTO.getChatRoomId();

        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        Member receiver = chatService.getReceiver(senderId, chatRoom);

        convenience(messageDTO, receiver, chatRoom);
    }

    @Operation(description = "파일 전송 처리")
    @PostMapping("/sendFile")
    public CommonApiResponse<?> sendFileInChatRoom(@RequestHeader("Authorization") String authorizationHeader,
                                                   @RequestPart("file") MultipartFile file,
                                                   @RequestParam Long chatRoomId) {
        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        ChatMessageDTO messageDTO = new ChatMessageDTO(
                fileService.getFileType(file.getContentType()),
                chatService.saveMultipartFile(file),
                file.getOriginalFilename(),
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
        websocketPublisher.convertAndSend("/sub/chats/" + messageDTO.getChatRoomId(), messageDTO);

        //FCM 알림
        FcmDTO fcmDTO = fcmService.createChatFcmDTO(messageDTO);
        fcmService.sendMessageTo(fcmDTO);

        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "채팅방 ID로 모든 채팅 메시지 조회")
    @GetMapping("/messages/{chatRoomId}")
    public CommonApiResponse<Slice<ChatMessageDTO>> findAllMessagesByChatRoom(@RequestParam int page, @RequestParam int size,
                                                                              @PathVariable Long chatRoomId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차순(최신순)
        Slice<ChatMessage> allMessages = chatService.findAllMessagesByChatRoomId(chatRoomId, pageable);

        return CommonApiResponse.createSuccess(allMessages.map(message -> new ChatMessageDTO(
                message.getMessageType(),
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
    public CommonApiResponse<?> findChatRoomById(@RequestParam Long chatRoomId, @RequestHeader("Authorization") String authorizationHeader) {
        Member loginMember = memberService.findMemberByToken(authorizationHeader);

        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        ChatRoomDTO chatRoomDTO = chatService.createChatRoomDTO(loginMember.getId(), chatRoom);

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

    //상담 연장 요청
    @Operation(summary = "상담 연장")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @PostMapping("/extend/{chatRoomId}")
    public CommonApiResponse<?> extendMatching(@RequestHeader("Authorization") String authorizationHeader,
                                               @PathVariable Long chatRoomId) {
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);

        if(chatRoom.getExtendState() == ExtendState.WAITING)
            throw new ExtendRequestConflictException();

        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        Member mentee = chatRoom.getMatching().getMentee();
        if(!loginMember.equals(mentee))
            throw new ForbiddenException();

        ChatMessageDTO messageDTO = ChatMessageDTO.createExtendRequest(loginMember, chatRoomId);
        convenience(messageDTO, loginMember, chatRoom);

        chatService.updateState(chatRoom);
        return CommonApiResponse.createSuccess();
    }

    @Operation(summary = "상담 연장 수락/거절")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "성공"),
            @ApiResponse(responseCode = "fail")})
    @PostMapping("/extend/request/{chatRoomId}")
    public CommonApiResponse<?> extendAcceptOrDecline(@RequestHeader("Authorization") String authorizationHeader,
                                                      @PathVariable Long chatRoomId,
                                                      @RequestParam DecisionStatus status) {
        ChatRoom chatRoom = chatService.findChatRoomById(chatRoomId);
        if(chatRoom.getExtendState() == ExtendState.EMPTY)
            throw new ExtendResponseConflictException();

        Member loginMember = memberService.findMemberByToken(authorizationHeader);
        Member mentor = chatRoom.getMatching().getMentor();
        if(!loginMember.equals(mentor))
            throw new ForbiddenException();

        ChatMessageDTO messageDTO = ChatMessageDTO.createExtendResponse(status, loginMember, chatRoomId);
        convenience(messageDTO, loginMember, chatRoom);
        
        chatService.updateState(chatRoom);

        if (status.equals(DecisionStatus.ACCEPT))
            matchingService.extendConsultTime(chatRoom.getMatching());

        return CommonApiResponse.createSuccess();
    }

    private void extracted(Long chatRoomId, int unreadMessageCount, LatestMessageDTO latestMessageDTO) {
        ChatUpdateDTO chatUpdateDTO = new ChatUpdateDTO(chatRoomId, unreadMessageCount, latestMessageDTO);
        log.info("Latest Message: " + latestMessageDTO.getContent());
        websocketPublisher.convertAndSend("/sub/unreadCount/" + chatRoomId, chatUpdateDTO);
    }

    private void convenience(ChatMessageDTO messageDTO, Member loginMember, ChatRoom chatRoom){
        // If both users are in the chat room, set the readCount to 2.
        chatService.setMessageReadCount(messageDTO);

        // If a file that has supported contentType is uploaded, save the file in S3 and return the URL.
        chatService.saveMessage(messageDTO);
        Member receiver = chatService.getReceiver(loginMember.getId(), chatRoom);

        // 메시지를 수신 하는 멤버의 unreadMessageCount를 호출
        int unreadMessageCount = chatService.getUnreadMessageCount(chatRoom.getId(), receiver.getId());

        // webSocket에 보내기
        websocketPublisher.convertAndSend("/sub/chats/" + messageDTO.getChatRoomId(), messageDTO);
        LatestMessageDTO latestChatMessage = createLatestMessageDTO(chatService.findLatestChatMessage(chatRoom.getId()));

        // 채팅 목록에 보내기
        extracted(chatRoom.getId(), unreadMessageCount, latestChatMessage);

        //FCM 알림
        FcmDTO fcmDTO = fcmService.createChatFcmDTO(messageDTO);
        fcmService.sendMessageTo(fcmDTO);
    }
}