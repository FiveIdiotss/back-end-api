package com.team.mementee.api.dto.memberDTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginMemberRequest {

    @NotEmpty(message = "이메일을 입력하세요.")
    private String email;

    @NotEmpty(message = "비밀번호를 입력하세요.")
    private String password;
}
