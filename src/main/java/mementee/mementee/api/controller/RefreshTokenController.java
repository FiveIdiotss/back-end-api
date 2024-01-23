package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.memberDTO.TokenDTO;
import mementee.mementee.api.domain.RefreshToken;
import mementee.mementee.api.repository.RefreshTokenRepository;
import mementee.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "Refresh Token 재발급")
public class RefreshTokenController {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    @PostMapping("/api/refresh")
    public ResponseEntity<TokenDTO> refresh(@RequestHeader("Authorization") String authorizationHeader) {

        // Authorization 헤더에서 리프레시 토큰 추출
        String refreshToken = extractRefreshToken(authorizationHeader);

        // 리프레시 토큰 유효성 검사
        RefreshToken storedRefreshToken = refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken);

        if (!storedRefreshToken.validateRefreshToken(refreshToken) || JwtUtil.isExpired(refreshToken, secretKey)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰");
        }

        // 리프레시 토큰이 유효하면 새로운 액세스 토큰 생성
        String email = storedRefreshToken.getEmail();
        String newAccessToken = JwtUtil.createAccessToken(email, secretKey);

        // 새 토큰을 클라이언트에 반환
        TokenDTO tokenDTO = new TokenDTO(newAccessToken, refreshToken);
        return ResponseEntity.ok(tokenDTO);
    }

    private String extractRefreshToken(String authorizationHeader) {
        // Authorization 헤더에서 토큰 부분 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            throw new IllegalArgumentException("잘못된 Authorization 헤더 형식");
        }
    }

}
