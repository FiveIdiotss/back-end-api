package com.team.mementee.api.service;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.RefreshToken;
import com.team.mementee.api.dto.memberDTO.TokenDTO;
import com.team.mementee.api.repository.RefreshTokenRepository;
import com.team.mementee.api.validation.TokenValidation;
import com.team.mementee.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RefreshTokenService {

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

        String newAccessToken = JwtUtil.createAccessToken(storedRefreshToken.getMember().getEmail());
        String newRefreshToken =  JwtUtil.createRefreshToken();

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
