package com.mementee.api.controller.memberDTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class LoginMemberRequest {

    @NotEmpty(message = "이메일을 입력하세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력하세요.")
    private String password;
}
