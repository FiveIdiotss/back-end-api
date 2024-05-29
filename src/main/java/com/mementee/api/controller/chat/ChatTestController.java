package com.mementee.api.controller.chat;

import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.mementee.api.dto.memberDTO.LoginMemberRequest;
import com.mementee.api.dto.memberDTO.LoginMemberResponse;
import com.mementee.api.service.ChatService;
import com.mementee.api.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatTestController {

    private final MemberService memberService;
    private final ChatService chatService;

    @GetMapping("/login")
    public String loginPage() {
        return "loginTestPages/login";
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String email, @RequestParam String password) {
        // AccessToken, SenderId
        LoginMemberResponse login = memberService.login(new LoginMemberRequest(email, password));
        String accessToken = login.getTokenDTO().getAccessToken();
        Long memberId = login.getMemberDTO().getId();

        ModelAndView mv = new ModelAndView("loginTestPages/index");
        mv.addObject("accessToken", accessToken);
        mv.addObject("senderId", memberId);

        // ChatRooms
        List<ChatRoom> chatRooms = chatService.findAllChatRoomByMemberId(memberId);

        List<ChatRoomDTO> chatRoomDTOs = chatRooms.stream()
                .map(chatRoom -> chatService.createChatRoomDTO(memberId, chatRoom))
                .toList();

        mv.addObject("chatRooms", chatRoomDTOs);
        return mv;
    }
}