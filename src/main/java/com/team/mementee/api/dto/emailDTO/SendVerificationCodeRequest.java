package com.team.mementee.api.dto.emailDTO;

import lombok.Data;

@Data
public class SendVerificationCodeRequest {
    private String email;
    private String univName;
}
