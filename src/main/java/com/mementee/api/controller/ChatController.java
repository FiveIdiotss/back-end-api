package com.mementee.api.controller;

import com.mementee.api.controller.chatDTO.ChatMessageDTO;
import com.mementee.api.controller.chatDTO.ChatRoomDTO;
import com.mementee.api.controller.redisDTO.RedisMessageSaveDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.service.ChatService;
import com.mementee.api.service.MemberService;
import com.mementee.config.chat.RedisPublisher;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@Tag(name = "실시간 채팅 기능")
public class ChatController {

    private final ChatService chatService;
    private final MemberService memberService;
    private final RedisTemplate<String, Object> redisTemplate; // Redis에 전달하는 핸들러
    private final RedisPublisher redisPublisher;
    private final SimpMessagingTemplate template; //websocket에 전달하는 핸들러

    @MessageMapping("/hello")
    public void sendMessage(final ChatMessageDTO message, Principal principal) {
        log.info("Controller(MessageMapping)={}", message);

        //websocket에 보내기
        template.convertAndSend("/sub/chats/52" , message.getContent());

        //redis에 Publish, redis에서 구독?
        redisTemplate.convertAndSend("chatRoom" + message.getReceiverId(), message);
    }

    @Operation(description = "채팅 메시지 보내기")
    @PostMapping("/message")
    public void saveSentChatMessage(@RequestBody ChatMessageDTO request, @RequestHeader("Authorization") String authorizationHeader) {
        Member loginMember = memberService.getMemberByToken(authorizationHeader);
        Member receiver = memberService.getMemberById(request.getReceiverId());
        System.out.println(receiver);

        // If a chatRoom exists between two members, use it. Otherwise, create a new chatRoom;
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(loginMember, receiver);

        // Create message with members and content and save.
        ChatMessage message = chatService.createMessage(request.getContent(), loginMember, chatRoom);

        chatService.saveMessage(message);

        System.out.println(message.getChatMessageId());

        RedisMessageSaveDTO redisMessageSaveDTO = new RedisMessageSaveDTO(
                message.getContent(),
                loginMember.getName(),
                message.getLocalDateTime());

        log.info("Publishing");
//        redisPublisher.publish(new ChannelTopic("ChatRoom" + chatRoom.getChatRoomId()), redisMessageSaveDTO);

        ListOperations<String, Object> stringObjectListOperations = redisTemplate.opsForList();
        stringObjectListOperations.rightPush(("chatRoom" + chatRoom.getChatRoomId()), redisMessageSaveDTO);
    }

    @Operation(description = "채팅방 ID로 모든 채팅 메시지 조회")
    @GetMapping("/messages")
    public ResponseEntity<List<RedisMessageSaveDTO>> findAllMessagesByChatRoom(@RequestParam Long chatRoomId) {
        log.info("chatRoomId={}", chatRoomId);

        List<ChatMessage> allMessages = chatService.findAllMessages(chatRoomId);

        List<RedisMessageSaveDTO> redisMessageDTOs = new ArrayList<>();

        for (ChatMessage message : allMessages) {
            RedisMessageSaveDTO chatMessageDTO = new RedisMessageSaveDTO(message.getContent(), message.getSender().getName(), message.getLocalDateTime());
            redisMessageDTOs.add(chatMessageDTO);
        }

        return ResponseEntity.ok(redisMessageDTOs);
    }

    @Operation(description = "멤버 별 채팅방 리스트 조회")
    @GetMapping("/chatRooms")
    public ResponseEntity<List<ChatRoomDTO>> findAllChatRoomsByMemberId(@RequestParam Long memberId) {
        Member member = memberService.getMemberById(memberId);

        List<ChatRoom> allChatRooms = chatService.findAllChatRoomByMember(member);

        List<ChatRoomDTO> chatRoomDTOs = new ArrayList<>();

        for (ChatRoom chatRoom : allChatRooms) {
            Long receiverId;
            String receiverName;

            // When member is sender.
            if (chatRoom.getSender().getId().equals(member.getId())) {
                receiverId = chatRoom.getReceiver().getId();
                receiverName = chatRoom.getReceiver().getName();
            }

            // member is receiver.
            else {
                receiverId = chatRoom.getSender().getId();
                receiverName = chatRoom.getSender().getName();
            }

            ChatRoomDTO chatRoomDTO = new ChatRoomDTO(chatRoom.getChatRoomId(), receiverId, receiverName);
            chatRoomDTOs.add(chatRoomDTO);
        }

        return ResponseEntity.ok(chatRoomDTOs);
    }

}


