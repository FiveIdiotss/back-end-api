package com.team.mementee.api.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.team.mementee.social.SocialLoginType;
import com.team.mementee.social.SocialMember;
import com.team.mementee.social.SocialToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OAuthService {

    private final List<SocialService> socialServiceList;

    public String requestAuthorizedURL(SocialLoginType socialLoginType) {
        SocialService socialService = this.findSocialServiceByType(socialLoginType);

        return socialService.getAuthorizedURL();
    }

    public SocialService findSocialServiceByType(SocialLoginType socialLoginType) {
        return socialServiceList.stream()
                .filter(socialService -> socialService.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Non-exist social login type"));
    }

    public SocialMember oAuthLogin(String code, SocialLoginType socialLoginType) throws JsonProcessingException {
        SocialService socialService = this.findSocialServiceByType(socialLoginType);

        // 요청한 토큰을 socialToken 형식에 맞게 변환하여 반환
        SocialToken socialToken = socialService.requestLoginToken(code);

        // 받은 토큰을 가지고 resouce server에게 사용자 정보를 요청 후 반환 받음
        String userInfo = socialService.requestUserInfo(socialToken);

        // 서버에서 받은 사용자 데이터를 SocialMember 형식에 맞춰서 반환
        return socialService.createSocialMember(userInfo, socialLoginType);
    }
}