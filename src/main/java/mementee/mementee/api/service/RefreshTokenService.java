package mementee.mementee.api.service;

import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.RefreshToken;
import mementee.mementee.api.repository.RefreshTokenRepository;
import mementee.mementee.security.JwtUtil;
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

    public String getAccessKey(String authorizationHeader){
        String refreshToken = authorizationHeader.split(" ")[1];

        Optional<RefreshToken> storedRefreshToken = refreshTokenRepository.findRefreshTokenByRefreshToken(refreshToken);

        if(storedRefreshToken.isPresent()){
            if (!storedRefreshToken.get().checkMatchRefreshToken(refreshToken)) {
                throw new IllegalArgumentException("잘못된 토큰");
            }
        }else {
            throw new IllegalArgumentException("잘못된 접근");
        }

        // 리프레시 토큰이 유효하면 새로운 액세스 토큰 생성
        String email = storedRefreshToken.get().getEmail();
        return JwtUtil.createAccessToken(email, secretKey);
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
