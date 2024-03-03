package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.chatDTO.saveMessageDTO;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.chat.ChatMessage;
import mementee.mementee.api.domain.chat.ChatRoom;
import mementee.mementee.api.repository.chat.ChatMessageRepository;
import mementee.mementee.api.repository.chat.ChatRoomRepository;
import mementee.mementee.api.service.ChatService;
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
