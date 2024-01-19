package mementee.mementee.api.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mementee.mementee.vo.SocialLoginType;
import mementee.mementee.vo.SocialMember;
import mementee.mementee.vo.SocialToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoLoginService implements SocialService {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String client_id;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String client_secret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirect_uri;
    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grant_type;
    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    private String authorization_uri;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String token_uri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String user_info_uri;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public String getAuthorizedURL() {
        return UriComponentsBuilder
                .fromUriString(authorization_uri)
                .queryParam("client_id", client_id)
                .queryParam("redirect_uri", redirect_uri)
                .queryParam("response_type", "code")
                .build()
                .encode()
                .toUriString();
    }

    // html form 형식으로 보내야 함
    public SocialToken requestLoginToken(Map<String, String> params) {
        String uri = UriComponentsBuilder
                .fromUriString(token_uri)
                .queryParam("grant_type", grant_type)
                .queryParam("client_id", client_id)
                .queryParam("client_secret", client_secret)
                .queryParam("redirect_uri", redirect_uri)
                .queryParam("code", params.get("code"))
                .build()
                .encode().toUriString();

        SocialToken socialToken = webClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .retrieve()
                .bodyToMono(SocialToken.class)
                .block();

        System.out.println("socialToken = " + socialToken);
        return socialToken;
    }

    // html form 형식으로 보내야 함
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

    @Override
    public SocialMember createSocialMember(String userInfo, SocialLoginType socialLoginType) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(userInfo);
        JsonNode accountNode = rootNode.get("kakao_account");
        JsonNode profileNode = accountNode.get("profile");

        return SocialMember.builder()
                .email(accountNode.get("email").asText())
                .nickname(profileNode.get("nickname").asText())
                .socialLoginType(socialLoginType)
                .build();
    }

    // 전송받은 유저 정보를 SocialMember 객체에 담아서 반환.
}
