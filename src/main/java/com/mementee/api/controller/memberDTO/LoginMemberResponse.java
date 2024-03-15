package com.mementee.api.controller.memberDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginMemberResponse {
    private MemberDTO memberDTO;
    private TokenDTO tokenDTO;
}
