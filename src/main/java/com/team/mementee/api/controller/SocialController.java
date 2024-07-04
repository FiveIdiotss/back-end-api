package com.team.mementee.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.team.mementee.api.dto.CommonApiResponse;
import com.team.mementee.api.service.social.OAuthService;
import com.team.mementee.social.SocialLoginType;
import com.team.mementee.social.SocialMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "소셜 로그인")
@RequestMapping("/api/login")
public class SocialController {

    private final OAuthService oAuthService;

    @Operation(summary = "해당 URL 클릭 시 해당 소셜 로그인 페이지로 이동")
    @GetMapping("/{socialLoginType}")
    public CommonApiResponse<String> socialLogin(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType) {
        String url = oAuthService.requestAuthorizedURL(socialLoginType);
        return CommonApiResponse.createSuccess(url);
    }

    @GetMapping("/oauth2/code/{socialLoginType}")
    public CommonApiResponse<SocialMember> socialLogins(@PathVariable SocialLoginType socialLoginType, @RequestParam String code) throws JsonProcessingException {
        return CommonApiResponse.createSuccess(oAuthService.oAuthLogin(code, socialLoginType));
    }
}
