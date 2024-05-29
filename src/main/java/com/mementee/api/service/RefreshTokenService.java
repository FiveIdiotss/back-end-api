package com.mementee.api.service;

import com.mementee.api.domain.Member;
import com.mementee.api.dto.memberDTO.TokenDTO;
import com.mementee.api.repository.RefreshTokenRepository;
import com.mementee.api.validation.TokenValidation;
import lombok.RequiredArgsConstructor;
import com.mementee.api.domain.RefreshToken;
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

    public Optional<RefreshToken> findRefreshTokenByMember(Member member){
        return refreshTokenRepository.findRefreshTokenByMember(member);
    }

    public Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken){
        return refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken);
    }

    @Transactional
    public TokenDTO getAccessKey(String authorizationHeader){
        RefreshToken storedRefreshToken = TokenValidation.isCheckStoredRefreshToken(findRefreshTokenByRefreshToken(authorizationHeader.split(" ")[1]));

        String newAccessToken = JwtUtil.createAccessToken(storedRefreshToken.getMember().getEmail(), secretKey);
        String newRefreshToken =  JwtUtil.createRefreshToken(secretKey);

        storedRefreshToken.updateToken(newRefreshToken);

        return new TokenDTO(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void save(RefreshToken refreshToken){
        refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public void deleteRefreshToken(Optional<RefreshToken> refreshToken){
        refreshToken.ifPresent(refreshTokenRepository::delete);
    }
}
