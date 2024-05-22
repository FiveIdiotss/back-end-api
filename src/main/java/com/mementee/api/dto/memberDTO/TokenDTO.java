package com.mementee.api.dto.memberDTO;

import com.mementee.api.domain.Member;
import com.mementee.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {
    private String accessToken;
    private String refreshToken;
}
