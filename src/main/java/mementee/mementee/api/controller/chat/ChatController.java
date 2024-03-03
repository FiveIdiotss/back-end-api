package mementee.mementee.api.controller.chat;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.repository.chat.ChatMessageRepository;
import mementee.mementee.api.repository.chat.ChatRoomRepository;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
}
