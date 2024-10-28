package com.team.mementee.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.mementee.api.dto.CommonApiResponse;
import com.team.mementee.api.dto.memberDTO.CustomMemberDetails;
import com.team.mementee.api.service.BlackListTokenService;
import com.team.mementee.api.service.CustomMemberService;
import com.team.mementee.config.error.ErrorCode;
import com.team.mementee.exception.unauthorized.InvalidTokenException;
import com.team.mementee.session.ServerSessionService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final BlackListTokenService bt;
    private final CustomMemberService customMemberService;
    private final ServerSessionService sessionService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

            //토큰이 없거나 Bearer으로 시작 안할시
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            //Token 꺼내기 (access, refresh)
            String token = authorization.split(" ")[1];

            //AccessToken이 BlackList에 있을 때
            if (bt.isCheckBlackList(token)) {
                log.info("블랙리스트에 있는 ACCESS TOKEN 입니다.");
                throw new InvalidTokenException();
            }

            // 세션이 존재하지 않을 경우, 세션을 생성함.
            HttpSession session = request.getSession(true);
            String email;

            // 세션이 존재할 경우 세션에서 사용자 이메일 정보 가져오기
            email = sessionService.get(session);

            // 세션이 존재하지만 세션 안에 값이 없을 경우 처리
//            if (email == null) {
//                log.info("세션은 존재하나 이메일 값이 없음.");
//            } else {
//                log.info("세션에서 이메일 추출.");
//            }


            // 세션에서 이메일을 가져오지 못한 경우, JWT 토큰을 파싱하여 이메일 추출
            if (email == null) {
                email = JwtUtil.getMemberEmail(token);
                //log.info("JWT 토큰에서 이메일 추출: {}", email);

                // 세션에 이메일 저장 (세션이 존재하는 경우에만 저장)
                if (email != null) {
                    sessionService.save(session, email);
                    //log.info("세션에 이메일 저장 완료: {}", email);
                }
            }

            String s = sessionService.get(session);
            //log.info("email에서 추출한 값={}", s);

            // 이메일이 여전히 null이라면 필터 체인 진행 후 반환
            if (email == null) {
                filterChain.doFilter(request, response);
                return;
            }

            //UserDetails에 회원 정보 객체 담기
            CustomMemberDetails customUserDetails = customMemberService.loadUserByUsername(email);

            //스프링 시큐리티 인증 토큰 생성
            Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

            //세션에 사용자 등록
            SecurityContextHolder.getContext().setAuthentication(authToken);
            filterChain.doFilter(request, response);
        } catch (InvalidTokenException | ExpiredJwtException | SecurityException | MalformedJwtException |
                 SignatureException | UnsupportedJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            CommonApiResponse<?> commonApiResponse = CommonApiResponse.createError(ErrorCode.INVALID_TOKEN.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(commonApiResponse));
        }
    }
}
