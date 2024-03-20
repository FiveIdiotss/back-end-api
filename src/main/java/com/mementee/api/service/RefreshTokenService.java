package com.mementee.api.service;

import com.mementee.api.dto.memberDTO.TokenDTO;
import lombok.RequiredArgsConstructor;
import com.mementee.api.domain.RefreshToken;
import com.mementee.api.repository.RefreshTokenRepository;
import com.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${spring.jwt.secret}")
    private String secretKey;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public TokenDTO getAccessKey(String authorizationHeader){
        String refreshToken = authorizationHeader.split(" ")[1];

        Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken);

        if(storedRefreshToken.isEmpty()){
            throw new IllegalArgumentException("잘못된 접근");
        }

        // 리프레시 토큰이 유효하면 새로운 액세스 토큰 생성, refreshToken 업데이트
        String email = storedRefreshToken.get().getEmail();

        String newAccessToken = JwtUtil.createAccessToken(email, secretKey);
        String newRefreshToken =  JwtUtil.createRefreshToken(secretKey);

        storedRefreshToken.get().updateToken(newRefreshToken);

        return new TokenDTO(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void save(RefreshToken refreshToken){
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findRefreshTokenByEmail(String email){
        return refreshTokenRepository.findRefreshTokenByEmail(email);
    }

    public void deleteRefreshToken(Optional<RefreshToken> refreshToken){
        refreshTokenRepository.deleteRefreshToken(refreshToken);
    }
}
