package com.mementee.api.service;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.repository.chat.ChatMessageRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;

    public void sendMessage(String content, Member member, ChatRoom chatRoom) {
        ChatMessage chatMessage = new ChatMessage(content, member, chatRoom, LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
    }

    public void saveChatRoom(ChatRoom chatRoom) {
        chatRoomRepository.save(chatRoom);
    }

    public void assignMessageToChatRoom(Long messageId, Long chatRoomId) {
        // 메시지와 채팅방 가져오기
        ChatMessage chatMessage = chatMessageRepository.findMessageById(messageId);
        ChatRoom chatRoom = chatRoomRepository.findChatRoomById(chatRoomId);

        // 채팅 메시지를 채팅방에 할당
        chatMessage.setChatRoom(chatRoom);
        chatMessageRepository.save(chatMessage);
    }

    // 센더, 리시버로 찾기, 멤버는 id값으로 등록되어 있음.

    // If a chatRoom exists between two members, use it. Otherwise, create a new chatRoom;
    @Transactional
    public ChatRoom findOrCreateChatRoom(Member sender, Member receiver) {
        ChatRoom chatRoom = chatRoomRepository.findBySendAndReceiver(sender, receiver);

        if (chatRoom == null) {
            System.out.println("create a new chatroom");
            chatRoom = new ChatRoom(sender,receiver);
            this.saveChatRoom(chatRoom);
        }
        else {
            System.out.println("use exist chatroom");
        }
        return chatRoom;
    }

    // 두 유저 사이의 채팅방을 호출
    public List<ChatMessage> findAllMessages(Member m1, Member m2) {
        ChatRoom chatRoom = chatRoomRepository.findBySendAndReceiver(m1, m2);
        return chatRoomRepository.findAllMessagesInChatRoom(chatRoom.getChatRoomId());
    }

}
