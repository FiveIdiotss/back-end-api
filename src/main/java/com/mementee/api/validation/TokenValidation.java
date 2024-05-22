package com.mementee.api.validation;

import com.mementee.api.domain.BlackListToken;
import com.mementee.api.domain.RefreshToken;
import com.mementee.api.repository.BlackListTokenRepository;
import com.mementee.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TokenValidation {

    //RefreshToken 으로 AccessToken 재발급시 DB에 RefreshToken 과 일치하는 정보가 있는지 검증
    public static RefreshToken isCheckStoredRefreshToken(Optional<RefreshToken> refreshToken){
        if(refreshToken.isEmpty()){
            throw new ForbiddenException();
        }
        return refreshToken.get();
    }
}
