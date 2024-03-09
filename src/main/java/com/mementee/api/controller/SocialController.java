package com.mementee.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mementee.api.service.social.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.mementee.vo.SocialLoginType;
import com.mementee.vo.SocialMember;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "소셜 로그인")
@RequestMapping("/api/login")
public class SocialController {

    private final OAuthService oAuthService;

    @Operation(description = "해당 URL 클릭 시 해당 소셜 로그인 페이지로 이동")
    @GetMapping("/{socialLoginType}")
    public ResponseEntity<String> socialLogin(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        String url = oAuthService.requestAuthorizedURL(socialLoginType);
        return ResponseEntity.ok().body(url);
    }

    @GetMapping("/oauth2/code/{socialLoginType}")
    public SocialMember socialLogins(@PathVariable SocialLoginType socialLoginType, @RequestParam String code) throws JsonProcessingException {
        return oAuthService.oAuthLogin(code, socialLoginType);
    }
}