package mementee.mementee.api.controller.chat;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.repository.chat.ChatMessageRepository;
import mementee.mementee.api.repository.chat.ChatRoomRepository;
import mementee.mementee.api.service.ChatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@Tag(name = "실시간 채팅 기능")
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatService chatService;


}
