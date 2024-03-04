package com.mementee.api.controller;

import com.mementee.api.controller.memberDTO.CreateMemberRequest;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.domain.enumtype.Gender;
import com.mementee.api.repository.chat.ChatMessageRepository;
import com.mementee.api.repository.chat.ChatRoomRepository;
import com.mementee.api.service.ChatService;
import com.mementee.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@Tag(name = "실시간 채팅 기능")
public class ChatController {

    private final ChatService chatService;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberService memberService;

    // 로그인 멤버가 리시버에서 메시지를 보낼 때 만약 서로가 등록 되어있는 채팅방이 존재하지 않으면 새로 만듦
    // 존재한다면 그 채팅방을 가져와서 사용
    // 센더, 리시버 상관없음, 두 유저가 연동된지만 확인하면 된다.


    @Operation(description = "채팅방 생성")
    @PostMapping("/create/chatRoom")
    public void createChatRoom() {
    }

    @Operation(description = "채팅 메시지 저장")
    @PostMapping("/create/message")
    public void saveSentChatMessage(@RequestParam String message, @RequestHeader("Authorization") String authorizationHeader) {

        System.out.println("faewfaewf    " + authorizationHeader);
        Member loginMember = memberService.getMemberByToken(authorizationHeader);

        Member member1 = memberService.findMemberByEmail("email");
        Member member2 = memberService.findMemberByEmail("이메일");
        Member member3 = memberService.findMemberByEmail("qwe");

        System.out.println(member1.getName());
        System.out.println(member2.getName());

        // If a chatRoom exists between two members, use it. Otherwise, create a new chatRoom;

        ChatRoom chatRoom = chatService.findOrCreateChatRoom(member3, member2);
        System.out.println(chatRoom.getSender().getName());

        chatService.sendMessage(message, member3, chatRoom);
    }

}
