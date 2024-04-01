package com.mementee.api.repository.chat;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.RefreshToken;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ChatRoomRepository {

    private final EntityManager em;

    // 채팅방 저장
    public void save(ChatRoom chatRoom) {
        log.info("새로운 채팅방이 데이터베이스에 저장되었습니다.");
        log.info("senderId={}, receiverId={}", chatRoom.getSender().getId(), chatRoom.getReceiver().getId());

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

    //
    public List<ChatRoom> findAllChatRoomsByMemberId(Long memberId) {
        String query = "SELECT cr FROM ChatRoom cr " +
                "WHERE cr.sender.id = :memberId OR cr.receiver.id = :memberId";

        return em.createQuery(query, ChatRoom.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }

    public Optional<ChatRoom> findChatRoomById(Member loginMember, Member receiver) {
        try {
            Long senderId = loginMember.getId();
            Long receiverId = receiver.getId();

            String query = "SELECT cr FROM ChatRoom cr " +
                    "WHERE (cr.sender.id = :senderId AND cr.receiver.id = :receiverId) " +
                    "OR (cr.sender.id = :receiverId AND cr.receiver.id = :senderId)";

            ChatRoom chatRoom = em.createQuery(query, ChatRoom.class)
                    .setParameter("senderId", senderId)
                    .setParameter("receiverId", receiverId)
                    .getSingleResult();
            return Optional.ofNullable(chatRoom);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

}