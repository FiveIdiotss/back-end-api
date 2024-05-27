package com.mementee.api.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mementee.social.SocialLoginType;
import com.mementee.social.SocialMember;
import com.mementee.social.SocialToken;
import org.springframework.stereotype.Service;

import static com.mementee.social.SocialLoginType.KAKAO;
import static com.mementee.social.SocialLoginType.NAVER;

@Service
public interface SocialService {
    String getAuthorizedURL();
    SocialToken requestLoginToken(String code);
    String requestUserInfo(SocialToken socialToken);
    SocialMember createSocialMember(String userInfo, SocialLoginType socialLoginType) throws JsonProcessingException;

    default SocialLoginType type() {
        if (this instanceof NaverLoginService) {
            return NAVER;
        }
        if (this instanceof KakaoLoginService) {
            return KAKAO;
        }
        return null;
    }

}
