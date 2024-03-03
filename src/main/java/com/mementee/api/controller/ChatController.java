package com.mementee.api.controller;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.repository.chat.ChatMessageRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import com.mementee.api.service.ChatService;
import com.mementee.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@Tag(name = "실시간 채팅 기능")
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatService chatService;
    private final MemberService memberService;


    @Operation(description = "채팅방 생성")
    @PostMapping("/create/chatRoom")
    public void createChatRoom() {
    }

    @Operation(description = "채팅 메시지 저장")
    @PostMapping("/create/message")
    public void saveSentChatMessage(@RequestParam String message) {
        ChatMessage chatMessage = new ChatMessage(message, new Member(), new ChatRoom(), LocalDateTime.now());
    }

}
