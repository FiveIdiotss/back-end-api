package com.team.mementee.api.controller.chat;

import com.team.mementee.api.domain.chat.ChatRoom;
import com.team.mementee.api.dto.chatDTO.ChatRoomDTO;
import com.team.mementee.api.dto.memberDTO.LoginMemberRequest;
import com.team.mementee.api.dto.memberDTO.LoginMemberResponse;
import com.team.mementee.api.service.ChatService;
import com.team.mementee.api.service.MemberService;
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

    @GetMapping("/")
    public String index() {
        return "loginTestPages/login";
    }
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

        log.info("memberId = {}", memberId);
        log.info("chatRoomDTOs = {}", chatRoomDTOs);

        mv.addObject("chatRooms", chatRoomDTOs);
        return mv;
    }

    @GetMapping("/notification-test")
    public String getNotificationPage() {
        return "loginTestPages/notificationCountPage";
    }

}