package mementee.mementee.api.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import mementee.mementee.vo.SocialLoginType;
import mementee.mementee.vo.SocialMember;
import mementee.mementee.vo.SocialToken;
import org.springframework.stereotype.Service;

import java.util.Map;

import static mementee.mementee.vo.SocialLoginType.KAKAO;
import static mementee.mementee.vo.SocialLoginType.NAVER;

@Service
public interface SocialService {
    String getAuthorizedURL();
    SocialToken requestLoginToken(Map<String, String> params);
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
