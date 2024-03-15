package com.mementee.api.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mementee.vo.SocialLoginType;
import com.mementee.vo.SocialMember;
import com.mementee.vo.SocialToken;
import org.springframework.stereotype.Service;

import static com.mementee.vo.SocialLoginType.KAKAO;
import static com.mementee.vo.SocialLoginType.NAVER;

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
