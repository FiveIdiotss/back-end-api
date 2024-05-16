package com.mementee.api.controller;

import com.mementee.api.dto.memberDTO.TokenDTO;
import com.mementee.api.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "Refresh Token을 이용하여 Access Token 재발급")
public class RefreshTokenController {

    private final RefreshTokenService refreshService;

    //refreshToken을 이용한 accessToken 재발급
    @GetMapping("/api/refresh")
    public ResponseEntity<?> updatedAccess(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            TokenDTO tokenDTO = refreshService.getAccessKey(authorizationHeader);
            return ResponseEntity.ok().body(tokenDTO);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

