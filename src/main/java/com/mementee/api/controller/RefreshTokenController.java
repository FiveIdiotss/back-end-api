package com.mementee.api.controller;

import com.mementee.api.dto.CommonApiResponse;
import com.mementee.api.dto.memberDTO.TokenDTO;
import com.mementee.api.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "Refresh Token을 이용하여 Access Token 재발급")
public class RefreshTokenController {

    private final RefreshTokenService refreshService;

    //refreshToken 을 이용한 accessToken 재발급
    @Operation(summary = "refreshToken 을 통해 accessToken 재발급 -> 자동 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "success", description = "발급 성공"),
            @ApiResponse(responseCode = "fail", description = "발급 실패")})
    @GetMapping("/api/refresh")
    public CommonApiResponse<TokenDTO> updatedAccess(@RequestHeader("Authorization") String authorizationHeader) {
            TokenDTO tokenDTO = refreshService.getAccessKey(authorizationHeader);
            return CommonApiResponse.createSuccess(tokenDTO);
    }
}

