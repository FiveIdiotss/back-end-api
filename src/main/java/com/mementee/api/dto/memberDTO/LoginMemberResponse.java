package com.mementee.api.dto.memberDTO;

import com.mementee.api.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginMemberResponse {
    private MemberDTO memberDTO;
    private TokenDTO tokenDTO;

    public static LoginMemberResponse createLoginMemberResponse(MemberDTO memberDTO, TokenDTO tokenDTO) {
        return new LoginMemberResponse(memberDTO, tokenDTO);
    }
}
