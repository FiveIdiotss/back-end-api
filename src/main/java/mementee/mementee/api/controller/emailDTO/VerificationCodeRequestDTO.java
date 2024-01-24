package mementee.mementee.api.controller.emailDTO;

import lombok.Data;

@Data
public class VerificationCodeRequestDTO {

    private String email;
    private String univName;

}
