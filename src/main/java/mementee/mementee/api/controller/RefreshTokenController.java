package mementee.mementee.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mementee.mementee.api.domain.RefreshToken;
import mementee.mementee.api.repository.RefreshTokenRepository;
import mementee.mementee.api.service.RefreshService;
import mementee.mementee.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@SecurityRequirement(name = "Bearer Authentication")
@RequiredArgsConstructor
@Tag(name = "Refresh Token을 이용하여 Access Token 재발급")
public class RefreshTokenController {

    private final RefreshService refreshService;

    @GetMapping("/api/refresh")
    public ResponseEntity<String> refresh(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String newAccessToken = refreshService.getAccessKey(authorizationHeader);
            return ResponseEntity.ok().body(newAccessToken);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("재발급 실패");
        }
    }
}

