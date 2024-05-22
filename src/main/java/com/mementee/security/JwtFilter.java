package com.mementee.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.service.BlackListTokenService;
import com.mementee.config.error.ErrorCode;
import com.mementee.exception.unauthorized.InvalidTokenException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    @Value("${spring.jwt.secret}")
    private final String secretKey;

    private final BlackListTokenService bt;

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

            //MemberEmail Token 에서 꺼내기
            String memberEmail = JwtUtil.getMemberEmail(token, secretKey);
            if (memberEmail == null) {
                //이 부분은 refreshToken 으로 accessToken 재발급시 refresh 토큰에는 사용자 정보가 없기 때문에 return
                filterChain.doFilter(request, response);
                return;
            }

            //권한부여
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(memberEmail, null, List.of(new SimpleGrantedAuthority("USER")));

            //Detail
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        } catch (InvalidTokenException | ExpiredJwtException | SecurityException | MalformedJwtException | UnsupportedJwtException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            CommonApiResponse<?> commonApiResponse = CommonApiResponse.createError(ErrorCode.INVALID_TOKEN.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(commonApiResponse));
        }
    }
}
