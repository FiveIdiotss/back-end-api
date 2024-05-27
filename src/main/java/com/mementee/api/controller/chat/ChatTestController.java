package com.mementee.api.controller.chat;

import com.mementee.api.dto.memberDTO.LoginMemberRequest;
import com.mementee.api.dto.memberDTO.LoginMemberResponse;
import com.mementee.api.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ChatTestController {

    private final MemberService memberService;

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/login";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(HttpServletResponse res, @RequestParam String email, @RequestParam String password) {
        LoginMemberResponse login = memberService.login(new LoginMemberRequest(email, password));
        String accessToken = login.getTokenDTO().getAccessToken();
        Cookie cookie = new Cookie("accessToken", accessToken);
        res.addCookie(cookie);
        System.out.println("accessToken: " + accessToken);
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index() {
        return "redirect:/index";
    }
}