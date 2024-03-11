//
//package com.mementee.api.repository.chat;
//
//import com.mementee.api.domain.chat.ChatMessage;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Commit;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@SpringBootTest
//@Transactional
//class ChatMessageRepositoryTest {
//
//    @Autowired
//    private ChatMessageRepository chatMessageRepository;
//
//    @PersistenceContext
//    private EntityManager em;
//
//    @Test
//    @Commit
//    void saveChatMessage() {
//        // given
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setContent("deleteTest");
//        chatMessage.setLocalDateTime(LocalDateTime.now());
//
//        // when
//        chatMessageRepository.save(chatMessage);
//        em.flush();
//        em.clear();
//
//        // then
//        ChatMessage savedMessage = chatMessageRepository.findMessageById(chatMessage.getChatMessageId());
//        System.out.println(savedMessage.getContent());
//        chatMessageRepository.deleteMessageById(chatMessage.getChatMessageId());
//        assertNotNull(savedMessage.getChatMessageId(), "메시지 저장 후 ID가 할당되어야 합니다.");
////        assertEquals("Sender1", savedMessage.getSender(), "발신자는 Sender여야 합니다.");
////        assertEquals("Receiver1", savedMessage.getContent(), "수신자는 Receiver여야 합니다.");
////        assertEquals("Content", savedMessage.getContent(), "메시지 내용은 'Test message'여야 합니다.");
//    }
//
//
////    @Test
////    @Commit
////    void deleteChatMessage() {
////        chatMessageRepository.deleteMessageById(2L);
////    }
//
//
//}
//
