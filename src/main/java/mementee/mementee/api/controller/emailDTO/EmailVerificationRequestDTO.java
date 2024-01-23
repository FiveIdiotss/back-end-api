package mementee.mementee.api.controller.emailDTO;

import lombok.Data;

@Data
public class EmailVerificationRequestDTO {

    private String key;
    private String email;
    private String univName;
    private boolean univ_check = true;

    public EmailVerificationRequestDTO(String key, String email, String univName) {
        this.key = key;
        this.email = email;
        this.univName = univName;
    }
}
