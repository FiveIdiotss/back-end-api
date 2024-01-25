package mementee.mementee.api.controller.memberDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginMemberResponse {
    private MemberDTO memberDTO;
    private String accessToken;
    private String refreshToken;
    private String message;
}
