package com.mementee.api.repository.chat;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.domain.enumtype.Gender;
import com.mementee.api.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
@Transactional
class ChatRoomRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired private ChatRoomRepository chatRoomRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;

    @BeforeEach
    @Commit
    void beforeEach() {
        Member member = new Member("dlwhdugs4147@gmail.com", "이종현", "qwer1234", 2025, Gender.MALE);
        memberRepository.save(member);

        Member member1 = new Member("xqy9xn", "현종이", "qwer1234", 2123, Gender.FEMALE);
        memberRepository.save(member1);
    }

    @Test
    @Commit
    void save() {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setSender(memberRepository.findOne(1L));
        chatRoom.setReceiver(memberRepository.findOne(2L));
        chatRoomRepository.save(chatRoom);
    }

    @Test
    void findChatRoomById() {
        ChatRoom findChatRoom = chatRoomRepository.findChatRoomById(52L);
        Member sender = findChatRoom.getSender();
        System.out.println(sender.getName());
    }

    @Test
    void findAllMessagesInChatRoom() {
        ChatRoom chatRoomById = chatRoomRepository.findChatRoomById(52L);
        ChatMessage chatMessage = new ChatMessage("Hello");
        ChatMessage chatMessage1 = new ChatMessage("Hi");
        ChatMessage chatMessage2 = new ChatMessage("wtf");
        ChatMessage chatMessage3 = new ChatMessage("testtest");

        chatMessageRepository.save(chatMessage);
        chatMessageRepository.save(chatMessage1);
        chatMessageRepository.save(chatMessage2);
        chatMessageRepository.save(chatMessage3);

        chatMessage.setChatRoom(chatRoomById);
        chatMessage1.setChatRoom(chatRoomById);
        chatMessage2.setChatRoom(chatRoomById);
        chatMessage3.setChatRoom(chatRoomById);

        List<ChatMessage> allMessagesInChatRoom = chatRoomRepository.findAllMessagesInChatRoom(chatRoomById.getChatRoomId());
        System.out.println("여기");

        for (ChatMessage message : allMessagesInChatRoom) {
            System.out.println(message);
        }
    }
}