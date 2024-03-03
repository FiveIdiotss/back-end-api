package com.mementee.api.repository.chat;

import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {

    private final EntityManager em;

    // 채팅방 저장
    public void save(ChatRoom chatRoom) {
        em.persist(chatRoom);
    }

    //Id로 채팅방 조회
    public ChatRoom findChatRoomById(Long id) {
        return em.find(ChatRoom.class, id);
    }

    public List<ChatMessage> findAllMessagesInChatRoom(Long chatRoomId) {
        return em.createQuery("SELECT cm FROM ChatMessage cm where cm.chatRoom.id = :chatRoomId", ChatMessage.class)
                .setParameter("chatRoomId", chatRoomId)
                .getResultList();
    }

    //채팅방 나가기



}
