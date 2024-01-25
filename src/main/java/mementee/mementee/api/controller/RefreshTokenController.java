package mementee.mementee.api.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.memberDTO.TestRequest;
import mementee.mementee.api.controller.memberDTO.TokenDTO;
import mementee.mementee.api.domain.RefreshToken;
import mementee.mementee.api.repository.RefreshTokenRepository;
import mementee.mementee.security.JwtUtil;
import org.antlr.v4.runtime.Token;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "Refresh Token을 이용하여 Access Token 재발급")
public class RefreshTokenController {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @GetMapping("/api/refresh")
    public ResponseEntity<String> refresh(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            // Authorization 헤더에서 리프레시 토큰 추출
            String refreshToken = authorizationHeader.split(" ")[1];
            // 리프레시 토큰 유효성 검사
            Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken);

            if (!storedRefreshToken.get().validateRefreshToken(refreshToken)) {
                throw new IllegalArgumentException("유효하지 않은 토큰");
            }

            // 리프레시 토큰이 유효하면 새로운 액세스 토큰 생성
            String email = storedRefreshToken.get().getEmail();
            String newAccessToken = JwtUtil.createAccessToken(email, secretKey);

            // 새 토큰을 클라이언트에 반환
            return ResponseEntity.ok().body(newAccessToken);

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("재발급 실패");
        }
    }
}

