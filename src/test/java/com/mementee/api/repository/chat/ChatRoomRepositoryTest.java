//package com.mementee.api.repository.chat;
//
//import com.mementee.api.domain.Member;
//import com.mementee.api.domain.chat.ChatMessage;
//import com.mementee.api.domain.chat.ChatRoom;
//import com.mementee.api.repository.member.MemberRepository;
//import com.mementee.api.service.MemberService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Commit;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//@Transactional
//class ChatRoomRepositoryTest {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired private ChatRoomRepository chatRoomRepository;
//    @Autowired private ChatMessageRepository chatMessageRepository;
//    @Autowired private MemberService memberService;
//
////    @BeforeEach
////    @Commit
////    void beforeEach() {
////        Member member = new Member("dlwhdugs4147@gmail.com", "이종현", "qwer1234", 2025, Gender.MALE);
////        memberRepository.save(member);
////
////        Member member1 = new Member("xqy9xn", "현종이", "qwer1234", 2123, Gender.FEMALE);
////        memberRepository.save(member1);
////
////        ChatRoom chatRoom = new ChatRoom(member, member1);
////        chatRoomRepository.save(chatRoom);
////    }
//
//    @Test
//    void save() {
//        ChatRoom chatRoom = new ChatRoom();
//        chatRoom.setSender(memberRepository.findOne(1L));
//        chatRoom.setReceiver(memberRepository.findOne(2L));
//        chatRoomRepository.save(chatRoom);
//    }
//
////    @Test
////    void findChatRoomById() {
////        ChatRoom findChatRoom = chatRoomRepository.findChatRoomById(52L);
////        Member sender = findChatRoom.getSender();
////        System.out.println(sender.getName());
////    }
//
//    @Test
//    void findAllMessagesInChatRoom() {
//        List<ChatMessage> allMessagesInChatRoom = chatRoomRepository.findAllMessagesInChatRoom(52L);
//        for (ChatMessage message : allMessagesInChatRoom) {
//            System.out.println("Content: " + message.getContent() + " Time: " + message.getLocalDateTime());
//        }
//    }
//
//    @Test
//    void findAllChatRoomsByMemberId() {
//        List<ChatRoom> allChatRoomsByMemberId = chatRoomRepository.findAllChatRoomsByMemberId(52L);
//
//        System.out.println("=================================================");
//
//        for (ChatRoom chatRoom : allChatRoomsByMemberId) {
//            System.out.println(chatRoom.getId());
//            System.out.println(chatRoom.getSender().getName());
//            System.out.println(chatRoom.getReceiver().getName());
//            System.out.println("=================================================");
//        }
//    }
//
////    @Test
////    void findBySendAndReceiver() {
////        ChatRoom bySendAndReceiver = chatRoomRepository.findBySendAndReceiver(memberService.findMemberByEmail("이메일"), memberService.findMemberByEmail("email"));
////        System.out.println(bySendAndReceiver.getReceiver());
////        System.out.println(bySendAndReceiver.getSender());
////    }
//
//
////    @Test
////    void findChatRoomBySenderAndReceiver() {
////        Optional<Long> chatRoomBySenderAndReceiver = chatRoomRepository.findChatRoomBySenderAndReceiver(2L, 3L);
////        System.out.println(chatRoomBySenderAndReceiver.get());
////    }
//
//
//}
