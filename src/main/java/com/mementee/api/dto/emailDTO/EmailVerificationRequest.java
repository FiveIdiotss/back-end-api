package com.mementee.api.dto.emailDTO;

import lombok.Data;

@Data
public class EmailVerificationRequest {

    private String email;
    private String univName;
    private String code;
}