package mementee.mementee.api.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.controller.emailDTO.EmailVerificationRequest;
import mementee.mementee.api.controller.emailDTO.SendVerificationCodeRequest;
import mementee.mementee.api.controller.emailDTO.TestObject;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SchoolEmailVerificationService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public String createRequestBody(SendVerificationCodeRequest request) throws JsonProcessingException {
        TestObject ob = new TestObject("ce6dc2f8-3d83-44ca-923a-7143022e5f3d", request.getEmail(), request.getUnivName());

        String hello = objectMapper.writeValueAsString(ob);
        System.out.println("Generate Json Data = " + hello);
        return hello;
    }

    public String sendEmailVerificationRequest(String requestBody) {
        String block = webClient.post()
                .uri("https://univcert.com/api/v1/certify")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(block);
        return block;
    }

    public String requestCertification(EmailVerificationRequest request) {
        Map<String, String> requestBody = new HashMap<>();

        requestBody.put("key", "ce6dc2f8-3d83-44ca-923a-7143022e5f3d");
        requestBody.put("email", request.getEmail());
        requestBody.put("univName", request.getUnivName());
        requestBody.put("code", request.getCode());

        return webClient.post()
                .uri("https://univcert.com/api/v1/certifycode")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}