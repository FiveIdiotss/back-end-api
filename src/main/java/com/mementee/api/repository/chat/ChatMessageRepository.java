package com.mementee.api.repository.chat;

import com.mementee.api.domain.chat.ChatMessage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatMessageRepository {

    private final EntityManager em;

    // 메시지 저장
    public void save(ChatMessage chatMessage) {
        em.persist(chatMessage);
    }

    //id로 메시지 조회
    public ChatMessage findMessageById(Long id) {
        return em.find(ChatMessage.class, id);
    }

    //메시지 삭제
    public void deleteMessageById(Long id) {
        ChatMessage message = findMessageById(id);
        em.remove(message);
    }

    public List<ChatMessage> findAllMessagesInChatRoom(Long chatRoomId) {
        String query = "SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :chatRoomId";
        return em.createQuery(query, ChatMessage.class)
                .setParameter("chatRoomId", chatRoomId)
                .getResultList();
    }

}
