package mementee.mementee.api.controller.emailDTO;

import lombok.Data;

@Data
public class SendVerificationCodeRequest {

    private String email;
    private String univName;

}
