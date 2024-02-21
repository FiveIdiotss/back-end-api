package com.mementee.api.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mementee.vo.SocialLoginType;
import com.mementee.vo.SocialMember;
import com.mementee.vo.SocialToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class NaverLoginService implements SocialService {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String client_id;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String client_secret;
    @Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
    private String redirect_uri;
    @Value("${spring.security.oauth2.client.registration.naver.authorization-grant-type}")
    private String grant_type;
    @Value("${spring.security.oauth2.client.provider.naver.authorization-uri}")
    private String authorization_uri;
    @Value("${spring.security.oauth2.client.provider.naver.token-uri}")
    private String token_uri;
    @Value("${spring.security.oauth2.client.provider.naver.user-info-uri}")
    private String user_info_uri;
    @Value("${spring.security.oauth2.client.provider.naver.user-name-attribute}")
    private String user_name_attribute;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public String getAuthorizedURL() {
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(authorization_uri)
                .queryParam("response_type", "code")
                .queryParam("client_id", client_id)
                .queryParam("redirect_uri", redirect_uri)
                .queryParam("state", "1234")
                .encode()
                .build();

        return uriComponents.toString();
    }

    public SocialToken requestLoginToken(String code) {
        String uri = UriComponentsBuilder
                .fromUriString(token_uri)
                .queryParam("client_id", client_id)
                .queryParam("client_secret", client_secret)
                .queryParam("grant_type", grant_type)
                .queryParam("code", code)
                .queryParam("state", "1234")
                .build()
                .encode().toUriString();

        return webClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToMono(SocialToken.class)
                .block();
    }

    public String requestUserInfo(SocialToken socialToken) {
        String uri = UriComponentsBuilder
                .fromUriString(user_info_uri)
                .build()
                .encode()
                .toUriString();

        return webClient.get()
                .uri(uri)
                .header("Authorization", "Bearer " + socialToken.getAccess_token())
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    // Resource Server에서 받아온 Json 데이터를 SocialMember로 변환
    public SocialMember createSocialMember(String userInfo, SocialLoginType socialLoginType) throws JsonProcessingException {
        // parameter로 넘어온 JSON 데이터를 JsonNode로 변환
        JsonNode rootNode = objectMapper.readTree(userInfo);

        // 사용자 정보에 해당하는 JsonNode만 추출
        JsonNode responseNode = rootNode.get(user_name_attribute);

        // 사용자 정보가 담겨진 JsonNode를 SocialMember 객체로 변환
        SocialMember socialMember = objectMapper.treeToValue(responseNode, SocialMember.class);
        socialMember.setSocialLoginType(socialLoginType);
        return socialMember;
    }

    //소셜 멤버를 일반 Member로 변환
}