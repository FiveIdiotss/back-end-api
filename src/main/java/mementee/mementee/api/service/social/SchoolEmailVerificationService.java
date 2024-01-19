package mementee.mementee.api.service.social;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchoolEmailVerificationService {

    private final WebClient webClient;
    private final Map<String, String> emailVerification = new HashMap<>();
    private final Map<String, String> requestCertification = new HashMap<>();

    @Value("${spring.jwt.secret}")      //JWT에 필요한 Key
    private String key;

    public String sendEmailVerificationRequest() {
        emailVerification.put("key", key);
        emailVerification.put("email", "jjh943202@syuin.ac.kr");
        emailVerification.put("univName", "삼육대학교");
        emailVerification.put("univ_check", "true");

        return webClient.post()
                .uri("https://univcert.com/api/v1/certify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(emailVerification)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public String requestCertification(String code) {
        requestCertification.put("key", key);
        requestCertification.put("email", "jjh943202@syuin.ac.kr");
        requestCertification.put("univName", "삼육대학교");
        requestCertification.put("code", code);

        return webClient.post()
                .uri("https://univcert.com/api/v1/certifycode")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestCertification)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}