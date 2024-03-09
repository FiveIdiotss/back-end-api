package com.mementee.api.repository.chat;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    //optional로 반환하도록 수정.
    // 두 유저 사이에 채팅방이 존재하는지 확인 후 존재하면 채팅방 Id 반환, 아니면 null 반환.
    public Long findBySendAndReceiver(Member sender, Member receiver) {
        Long senderId = sender.getId();
        Long receiverId = receiver.getId();

        // sender가 senderId이고 receiver가 receiverId인 채팅방을 찾거나,
        // sender가 receiverId이고 receiver가 senderId인 채팅방을 찾음.
        String query = "SELECT cm.chatRoomId FROM ChatRoom cm " +
                "WHERE (cm.sender.id = :senderId AND cm.receiver.id = :receiverId) " +
                "OR (cm.sender.id = :receiverId AND cm.receiver.id = :senderId)";

        try {
            return em.createQuery(query, Long.class)
                    .setParameter("senderId", senderId)
                    .setParameter("receiverId", receiverId)
                    .getSingleResult();

        } catch (NoResultException e) {
            System.out.println("두 사람 사이에 채팅방이 존재하지 않습니다.");
            return null; // 채팅방이 없을 경우 null을 반환합니다.
        }
    }

    //
    public List<ChatRoom> findAllChatRoomsByMemberId(Long memberId) {
        String query = "SELECT cr FROM ChatRoom cr " +
                "WHERE cr.sender.id = :memberId OR cr.receiver.id = :memberId";

        return em.createQuery(query, ChatRoom.class)
                .setParameter("memberId", memberId)
                .getResultList();
    }
}