//package mementee.mementee.api.service;
//
//import mementee.mementee.api.domain.Member;
//import mementee.mementee.api.domain.chat.ChatMessage;
//import mementee.mementee.api.domain.chat.ChatRoom;
//import mementee.mementee.api.repository.MemberRepository;
//import mementee.mementee.api.repository.chat.ChatMessageRepository;
//import mementee.mementee.api.repository.chat.ChatRoomRepository;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class ChatServiceTest {
//
//    @Autowired
//    private ChatService chatService;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private ChatRoomRepository chatRoomRepository;
//
//    @Autowired
//    private ChatMessageRepository chatMessageRepository;
//
//    @Test
//    public void testAssignMessageToChatRoom() {
//        // 가짜 멤버 생성
//        Member user1 = new Member();
//        user1.setName("User1");
//        memberRepository.save(user1);
//
//        Member user2 = new Member();
//        user2.setName("User2");
//        memberRepository.save(user2);
//
//        // 가짜 채팅방 생성
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.setReceiver(user1);
//        chatRoom.setSender(user2);
//        chatRoomRepository.save(chatRoom);
//
//        // 가짜 채팅 메시지 생성
//        ChatMessage chatMessage = new ChatMessage();
//        chatMessage.setSender(user1);
//        chatMessage.setLocalDateTime(LocalDateTime.now());
//        chatMessage.setContent("TestContent");
//        chatMessage.setChatRoom(chatRoom);
//        chatMessageRepository.save(chatMessage);
//
//        // 채팅 메시지를 특정 채팅방에 할당하고 저장
//        chatService.assignMessageToChatRoom(chatMessage.getChatMessageId(), chatRoom.getChatRoomId());
//
//        // 특정 채팅방에 있는 메시지 조회
//        List<ChatMessage> messagesInChatRoom = chatRoomRepository.findAllMessagesInChatRoom(chatRoom.getChatRoomId());
//
//        // 테스트
//        assertNotNull(messagesInChatRoom);
//        assertEquals(1, messagesInChatRoom.size());
//        assertEquals(chatMessage.getContent(), messagesInChatRoom.get(0).getContent());
//    }
//}