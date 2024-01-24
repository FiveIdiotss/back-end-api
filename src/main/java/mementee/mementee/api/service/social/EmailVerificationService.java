package mementee.mementee.api.service.social;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mementee.mementee.api.controller.emailDTO.EmailVerificationRequest;
import mementee.mementee.api.controller.emailDTO.SendVerificationCodeRequest;
import mementee.mementee.api.controller.emailDTO.CodeVerificationDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationService {

    @Value("${api.univcert.key}")
    private String univcertApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    /**
     * JSON Request
     * {
     *  "key" : "부여받은 API KEY",
     *  "email" : "abc@mail.hongik.ac.kr",
     *  "univName" : "홍익대학교",
     *  "univ_check" : true
     *   (true라면 해당 대학 재학 여부, false라면 메일 소유 인증만)
     * }
     */
    public ResponseEntity<String> verificationCodeRequest(String requestBody) {
        try {

            // body에 json 데이터를 담어서 post로 univcert 서버에 보낸 후 받은 데이터를 String(JSON)으로 가공
            String response = webClient.post()
                    .uri("https://univcert.com/api/v1/certify")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {

            // univcert에서 발생시킨 에러의 status 코드와 메시지를 웹에 반환
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }
    }

    /**
     * {
     *  "key" : "부여받은 API KEY",
     *  "email" : "abc@mail.hongik.ac.kr",
     *  "univName" : "홍익대학교",
     *  "code" : 3816
     * }
     */
    public ResponseEntity<String> requestCertification(EmailVerificationRequest request) {
        // 요청에 필요한 request 만드는 과정
        Map<String, String> requestBody = createRequestBodyWithKeyValue();

        requestBody.put("email", request.getEmail());
        requestBody.put("univName", request.getUnivName());
        requestBody.put("code", request.getCode());

        try {
            String response = webClient.post()
                    .uri("https://univcert.com/api/v1/certifycode")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }

    }

    /**
     * {
     *  "key" : "부여받은 API KEY"
     * }
     */
    public String resetVerifiedUsers() {
        Map<String, String> requestBody = createRequestBodyWithKeyValue();

        return webClient
                .post()
                .uri("https://univcert.com/api/v1/clear")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * 요청 주소: POST / https://univcert.com/api/v1/clear/초기화 하고 싶은 email
     * {
     *  "key" : "부여받은 API KEY"
     * }
     */
    public ResponseEntity<String> resetVerifiedUserByEmail(String email) {
        Map<String, String> requestBody = createRequestBodyWithKeyValue();

        try {
            String response = webClient
                    .post()
                    .uri("https://univcert.com/api/v1/clear/" + email)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return ResponseEntity.ok(response);
        } catch (WebClientResponseException e) {
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(e.getResponseBodyAsString());
        }
    }

    public String createRequestBodyForCode(SendVerificationCodeRequest request) {
        try {
            CodeVerificationDTO ob = new CodeVerificationDTO(univcertApiKey, request.getEmail(), request.getUnivName());

            return objectMapper.writeValueAsString(ob);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Json 데이터 처리 중 오류 발생" + e.getMessage());
        }
    }

    public Map<String, String> createRequestBodyWithKeyValue() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("key", univcertApiKey);

        return requestBody;
    }
}