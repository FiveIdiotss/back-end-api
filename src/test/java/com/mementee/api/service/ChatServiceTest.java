//package com.mementee.api.service;
//
//import com.mementee.api.controller.chatDTO.ChatMessageDTO;
//import com.mementee.api.domain.Member;
//import com.mementee.api.domain.chat.ChatMessage;
//import com.mementee.api.domain.chat.ChatRoom;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
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
//        ChatRoom chatRoom = chatService.findOrCreateChatRoom(email1, email);
//        System.out.println(chatRoom.getReceiver().getName());
//        System.out.println(chatRoom.getSender().getName());
//    }
//
////    @Test
////    void findLatestMessage() {
////        ChatMessage latestChatMessage = chatService.findLatestChatMessage(52L);
////        ChatMessageDTO chatMessageDTO = new ChatMessageDTO(latestChatMessage.getContent(), latestChatMessage.getSender().getId(), latestChatMessage.getLocalDateTime());
////        System.out.println(chatMessageDTO);
////    }
//}