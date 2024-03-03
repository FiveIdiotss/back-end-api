package mementee.mementee.api.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.chat.ChatMessage;
import mementee.mementee.api.domain.chat.ChatRoom;
import mementee.mementee.api.repository.chat.ChatMessageRepository;
import mementee.mementee.api.repository.chat.ChatRoomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    public void assignMessageToChatRoom(Long messageId, Long chatRoomId) {
        // 메시지와 채팅방 가져오기
        ChatMessage chatMessage = chatMessageRepository.findMessageById(messageId);
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(chatRoomId);

        // 채팅 메시지를 채팅방에 할당
        chatMessage.setChatRoom(chatRoom);
        chatMessageRepository.save(chatMessage);
    }
}
