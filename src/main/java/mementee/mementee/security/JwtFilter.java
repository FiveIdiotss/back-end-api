package mementee.mementee.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.service.BlackListTokenService;
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
            //System.out.println("authorization 상태: " + authorization);

            if (authorization == null || !authorization.startsWith("Bearer ")) {       //토큰이 없거나 Bearer으로 시작 안할시
                //System.out.println("authentication 을 잘 못 보냈습니다.");
                filterChain.doFilter(request, response);
                return;
            }

            //Token 꺼내기 (access, refresh)
            String token = authorization.split(" ")[1];

            //accessToken이 blackList에 있을 때
            if(bt.isCheckBlackList(token)){
                filterChain.doFilter(request, response);
                return;
            }

            //Token Expired 되었는지 여부
            if (JwtUtil.isExpired(token, secretKey)) {
                //System.out.println("Token 이 만료 되었습니다.");
                filterChain.doFilter(request, response);
                return;
            }

            //MemberEmail Token 에서 꺼내기
            String memberEmail = JwtUtil.getMemberEmail(token, secretKey);
            if(memberEmail == null){
                //이 부분은 refreshToken 으로 accessToken 재발급시 refresh토큰에는 사용자 정보가 없기 때문에 return
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
        }catch (ExpiredJwtException e){
            System.out.println("만료됨");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 만료됨");
        }
    }

}
