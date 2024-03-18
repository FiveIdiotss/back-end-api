package com.mementee.api.dto.emailDTO;

import lombok.Data;

@Data
public class CodeVerificationDTO {

    private String key;
    private String email;
    private String univName;
    private boolean univ_check = true;

    public CodeVerificationDTO(String key, String email, String univName) {
        this.key = key;
        this.email = email;
        this.univName = univName;
    }
}
