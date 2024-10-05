package com.team.mementee.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class ServerSessionService {

    public void save(HttpSession session, String email) {
        session.setAttribute("email", email); // 토큰을 키로 하고 이메일을 값으로 세션에 저장
    }

    public String get(HttpSession session) {
        return (String) session.getAttribute("email"); // 세션에서 이메일 가져오기
    }
}
