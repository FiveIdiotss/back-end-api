package com.mementee.api.controller;

import com.mementee.api.controller.chatDTO.ChatMessageDTO;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.repository.chat.ChatRoomRepository;
import com.mementee.api.service.ChatService;
import com.mementee.api.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
@Tag(name = "실시간 채팅 기능")
public class ChatController {

    private final ChatService chatService;
    private final MemberService memberService;

    // 로그인 멤버가 리시버에서 메시지를 보낼 때 만약 서로가 등록 되어있는 채팅방이 존재하지 않으면 새로 만듦
    // 존재한다면 그 채팅방을 가져와서 사용
    // 센더, 리시버 상관없음, 두 유저가 연동된지만 확인하면 된다.
    @Operation(description = "채팅방 생성")
    @PostMapping("/create/chatRoom")
    public void createChatRoom() {

    }

    @Operation(description = "채팅 메시지 읽기")
    @GetMapping("/create/message")
    public void saveSentChatMessage(@RequestBody ChatMessageDTO request, @RequestHeader("Authorization") String authorizationHeader) {
        Member loginMember = memberService.getMemberByToken(authorizationHeader);
        log.info("loginMember={}", loginMember.getName());

        Long receiverId = request.getReceiverId();
        String content = request.getContent();
        System.out.println("receiverId = " + receiverId);
        System.out.println("content = " + content);

        Member receiver = memberService.getMemberById(receiverId);
        log.info("receiver={}", receiver.getName());

        // If a chatRoom exists between two members, use it. Otherwise, create a new chatRoom;
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(loginMember, receiver);

        chatService.sendMessage(request.getContent(), loginMember, chatRoom);
    }

//    @Operation(description = "채팅 메시지 저장")
//    @PostMapping("/message")
//    public void test(Long ) {
//        Member receiver = memberService.getMemberById(52L);
//        log.info("receiver={}", receiver.getName());
//
//        // If a chatRoom exists between two members, use it. Otherwise, create a new chatRoom;
//        ChatRoom chatRoom = chatService.findOrCreateChatRoom(loginMember, receiver);
//
//        chatService.sendMessage(request.getContent(), loginMember, chatRoom);
//    }

    @Operation(description = "채팅 메시지 조회")
    @GetMapping("/messages")
    public ResponseEntity<List<ChatMessage>> findAllMessagesByChatRoom() {
//        Member loginMember = memberService.getMemberByToken(authorizationHeader);

        Member member1 = memberService.findMemberByEmail("email");
        Member member2 = memberService.findMemberByEmail("이메일");

        List<ChatMessage> allMessages = chatService.findAllMessages(member1, member2);

        for (ChatMessage message : allMessages) {
            System.out.println("content: " + message.getContent() + " time: " + message.getLocalDateTime());
        }

        return new ResponseEntity<>(allMessages, HttpStatus.OK);
    }

}
