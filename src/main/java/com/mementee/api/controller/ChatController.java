package com.mementee.api.controller;

import com.mementee.api.domain.enumtype.FileType;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import com.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.dto.notificationDTO.FcmDTO;
import com.mementee.api.service.*;
import com.mementee.config.chat.RedisPublisher;
import com.mementee.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.joda.time.LocalDateTime;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.listener.ChannelTopic;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
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
    private final SimpMessagingTemplate websocketPublisher; //websocket에 전달하는 핸들러
    private final RedisPublisher redisPublisher;

    private final NotificationService notificationService;
    private final FCMNotificationService fcmNotificationService;

    @MessageMapping("/hello")
    public void sendMessage(ChatMessageDTO messageDTO) throws IOException {

//        if (messageDTO.getImage() != null) {
//            String imageUrl = chatService.saveImage(messageDTO.getImage());
//            messageDTO.setImage(imageUrl);
//        }
//
//        if (isAllConnected(messageDTO.getChatRoomId())) messageDTO.setReadCount(0);
//        else messageDTO.setReadCount(1);


        // redis에 publish
        redisPublisher.publish(ChannelTopic.of("chatRoom" + messageDTO.getChatRoomId()), messageDTO);

        // webSocket에 보내기
        websocketPublisher.convertAndSend("/sub/chats/" + messageDTO.getChatRoomId(), messageDTO);

        //DB에 저장
        chatService.saveMessage(messageDTO);

        //FCM 알림
        FcmDTO fcmDTO = fcmNotificationService.createChatFcmDTO(messageDTO);
        fcmNotificationService.sendMessageTo(fcmDTO);

        //redis
        //redisPublisher.publish(ChannelTopic.of("chatRoom" + messageDTO.getChatRoomId()), messageDTO);
        //SSE 알림
        //notificationService.sendNotification(receiverId, messageDTO);
    }


    //video, picture, zip file, pdf file, 연락처
    @Operation(description = "파일 전송 처리")
    @PostMapping("/sendFile")
    public ResponseEntity<String> sendFile(@RequestParam("file") MultipartFile file) {
        // If file is not uploaded, return BAD_REQUEST error.
        if (file.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded.");

        // If a file that has supported contentType is uploaded, save the file in S3 and return the URL.
        return ResponseEntity.status(HttpStatus.OK).body(chatService.save(file));
    }

    @Operation(description = "채팅방 ID로 모든 채팅 메시지 조회")
    @GetMapping("/messages/{chatRoomId}")
    public Slice<ChatMessageDTO> findAllMessagesByChatRoom(@RequestParam int page, @RequestParam int size,
                                                           @PathVariable Long chatRoomId) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending()); //내림차순(최신순)
        Slice<ChatMessage> allMessages = chatService.findAllMessagesByChatRoomId(chatRoomId, pageable);

        return allMessages.map(message -> new ChatMessageDTO(
                message.getContent(),
                message.getSender().getName(),
                message.getSender().getId(),
                message.getChatRoom().getId(),
                message.getImage(),
                message.getLocalDateTime()
        ));
    }

    @Operation(description = "상대방 ID로 해당 채팅방 조회 " +
            "상대방 프로필을 조회하고 메시지를 보낼 때, 둘 사이에 채팅방이 존재하는지 확인(채팅방이 존재하지 않으면 새로 만듦)")
    @GetMapping("/chatRoom")
    public ResponseEntity<?> findChatRoomByReceiverId(@RequestParam Long receiverId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Member loginMember = memberService.getMemberByToken(authorizationHeader);
            Member receiver = memberService.getMemberById(receiverId);

            Optional<ChatRoom> chatRoom = chatService.findChatRoom(loginMember, receiver);

            ChatRoomDTO chatRoomDTO = new ChatRoomDTO(chatRoom.get().getId(), receiverId, receiver.getName());
            return ResponseEntity.ok(chatRoomDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(description = "특정 멤버가 속한 채팅방 모두 조회")
    @GetMapping("/chatRooms")
    public ResponseEntity<List<ChatRoomDTO>> findAllChatRoomsByMemberId(@RequestParam Long memberId) {
        List<ChatRoom> allChatRooms = chatService.findAllChatRoomByMemberId(memberId);

        List<ChatRoomDTO> chatRoomDTOs = allChatRooms.stream()
                .map(chatRoom -> chatService.createChatRoomDTO(memberId, chatRoom))
                .collect(Collectors.toList());

        return ResponseEntity.ok(chatRoomDTOs);
    }
}