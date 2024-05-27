package com.mementee.api.controller;

import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.emailDTO.EmailVerificationRequest;
import com.mementee.api.dto.emailDTO.SendVerificationCodeRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mementee.api.service.social.EmailVerificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "이메일 인증")
@RequestMapping("/api/email")
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    @Operation(description = "이메일 인증 코드 전송")
    @PostMapping
    public ResponseEntity<String> requestVerificationCode(@RequestBody SendVerificationCodeRequest request) {
        // @RequestBody로 email 및 univName 받아서 univcert 요청에 필요한 json 데이터로 가공.
        String requestBody = emailVerificationService.createRequestBodyForCode(request);

        // 서버에서 받은 responseBody를 반환
        return emailVerificationService.verificationCodeRequest(requestBody);
    }

    @Operation(description = "전송된 코드로 서버에 인증 요청")
    @PostMapping("/verify")
    public ResponseEntity<String> emailVerification(@RequestBody EmailVerificationRequest request) {
        return emailVerificationService.requestCertification(request);
    }

    @Operation(description = "인증된 특정 이메일 초기화")
    @PostMapping("/resetByEmail")
    public ResponseEntity<String> resetVerifiedUserByEmail(@RequestParam String email) {
        return emailVerificationService.resetVerifiedUserByEmail(email);
    }

    @Operation(description = "인증된 유저 이메일 모두 초기화")
    @PostMapping("/resetAll")
    public String resetVerifiedUsers() {
        return emailVerificationService.resetVerifiedUsers();
    }
}
