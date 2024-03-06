package com.mementee.api.controller;

import com.mementee.api.controller.chatDTO.ChatMessageDTO;
import com.mementee.api.controller.chatDTO.ChatRoomDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.service.ChatService;
import com.mementee.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    private final RedisTemplate<String, Object> redisTemplate;

    // 로그인 멤버가 리시버에서 메시지를 보낼 때 만약 서로가 등록 되어있는 채팅방이 존재하지 않으면 새로 만듦
    // 존재한다면 그 채팅방을 가져와서 사용
    // 센더, 리시버 상관없음, 두 유저가 연동된지만 확인하면 된다.

    @Operation(description = "채팅 메시지 읽기")
    @PostMapping("/create/message")
    public void saveSentChatMessage(@RequestBody ChatMessageDTO request, @RequestHeader("Authorization") String authorizationHeader) {
        Member loginMember = memberService.getMemberByToken(authorizationHeader);
        Member receiver = memberService.getMemberById(request.getReceiverId());

        // If a chatRoom exists between two members, use it. Otherwise, create a new chatRoom;
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(loginMember, receiver);

        // Create message with members and content and save.
        ChatMessage message = chatService.createMessage(request.getContent(), loginMember, chatRoom);
        chatService.saveMessage(message);

        System.out.println(message.getChatMessageId());

//        redisService.setValues("chat", "helloTest");
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(String.valueOf(message.getChatMessageId()), message.getContent());
    }

    @Operation(description = "채팅 메시지 조회")
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> findAllMessagesByChatRoom(@RequestBody ChatMessageDTO request, @RequestHeader("Authorization") String authorizationHeader) {
        Member loginMember = memberService.getMemberByToken(authorizationHeader);
        Member receiver = memberService.getMemberById(request.getReceiverId());

        List<ChatMessage> allMessages = chatService.findAllMessages(loginMember, receiver);

        List<ChatMessage> responseDTOs = allMessages.stream()
                .map(message -> new ChatMessage(message.getContent(), message.getLocalDateTime()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }

    @Operation(description = "멤버 별 채팅방 리스트 조회")
    @GetMapping("/chatRooms")
    public ResponseEntity<List<ChatRoomDTO>> findAllChatRoomsByMemberId(@RequestHeader("Authorization") String authorizationHeader) {
        Member loginMember = memberService.getMemberByToken(authorizationHeader);

        List<ChatRoom> allChatRooms = chatService.findAllChatRoomByMember(loginMember);

        List<ChatRoomDTO> chatRoomDTOs = new ArrayList<>();

        for (ChatRoom chatRoom : allChatRooms) {
            ChatRoomDTO chatRoomDTO = new ChatRoomDTO(chatRoom.getChatRoomId(), chatRoom.getSender().getName(), chatRoom.getReceiver().getName());
            chatRoomDTOs.add(chatRoomDTO);
        }

        return ResponseEntity.ok(chatRoomDTOs);
    }
}