package mementee.mementee.api.repository.chat;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.chat.ChatRoom;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChatRoomRepository {

    private final EntityManager em;

    public void save(ChatRoom chatRoom) {
        em.persist(chatRoom);
    }

    // 채팅방 이름으로 조회
    public ChatRoom findChatRoomByRoomName(String roomName) {
        return em.find(ChatRoom.class, roomName);
    }
}
