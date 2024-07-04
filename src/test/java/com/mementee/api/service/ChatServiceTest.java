//package com.mementee.api.service;
//
//import com.team.domain.api.mementee.Member;
//import com.team.chat.domain.api.mementee.ChatMessage;
//import com.team.chat.domain.api.mementee.ChatRoom;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@SpringBootTest
//@Transactional
//class ChatServiceTest {
//
//    @Autowired private ChatService chatService;
//    @Autowired private MemberService memberService;
//
//    @Test
//    void findChatRoom() {
//        Member email = memberService.findMemberByEmail("1234");
//        Member email1 = memberService.findMemberByEmail("2345");
//
//    }
//
////    @Test
////    void findLatestMessage() {
////        ChatMessage latestChatMessage = chatService.findLatestChatMessage(52L);
////        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(latestChatMessage.getContent(), latestChatMessage.getSender().getId(), latestChatMessage.getLocalDateTime());
////        System.out.println(chatMessageDTO);
////    }
//    @Test
//    void findLatestChatMessage() {
//        ChatMessage latestChatMessage = chatService.findLatestChatMessage(1L).get();
//        System.out.println(latestChatMessage);
//    }
//
//}