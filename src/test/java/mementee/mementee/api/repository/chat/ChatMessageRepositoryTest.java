package mementee.mementee.api.repository.chat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mementee.mementee.api.domain.Member;
import mementee.mementee.api.domain.School;
import mementee.mementee.api.domain.chat.ChatMessage;
import mementee.mementee.api.domain.chat.ChatRoom;
import mementee.mementee.api.domain.enumtype.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChatMessageRepositoryTest {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void saveChatMessage() {

        Member member2 = new Member("bbb@naver.com", "김민기", "1234", 2024 , Gender.MALE);
        Member member3 = new Member("ccc@naver.com", "이종현", "1234", 2010,  Gender.MALE);


        Member member = new Member();
        member.setName("sender1");

        Member member1 = new Member();
        member.setName("receiver1");

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSender(member2);
        chatRoom.setReceiver(member3);

        // given
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContent("HelloWorld");
        chatMessage.setSender(member2);
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setLocalDateTime(LocalDateTime.now());

        // when
//        chatMessageRepository.save(chatMessage);

        em.persist(chatRoom);
        em.persist(chatMessage);
        em.flush();
        em.clear();

        // then
        ChatMessage savedMessage = em.find(ChatMessage.class, chatMessage.getChatMessageId());
        assertNotNull(savedMessage.getChatMessageId(), "메시지 저장 후 ID가 할당되어야 합니다.");
        assertEquals("Sender1", savedMessage.getSender(), "발신자는 Sender여야 합니다.");
        assertEquals("Receiver1", savedMessage.getContent(), "수신자는 Receiver여야 합니다.");
        assertEquals("Content", savedMessage.getContent(), "메시지 내용은 'Test message'여야 합니다.");
    }

}