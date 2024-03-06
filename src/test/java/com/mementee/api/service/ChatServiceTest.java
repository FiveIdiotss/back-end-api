//package com.mementee.api.service;
//
//import com.mementee.api.domain.Member;
//import com.mementee.api.domain.chat.ChatRoom;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.junit.jupiter.api.Assertions.*;
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
//        Member email = memberService.findMemberByEmail("email");
//        Member email1 = memberService.findMemberByEmail("이메일");
//
//        ChatRoom chatRoom = chatService.findOrCreateChatRoom(email1, email);
//        System.out.println(chatRoom.getReceiver().getName());
//        System.out.println(chatRoom.getSender().getName());
//    }
//}