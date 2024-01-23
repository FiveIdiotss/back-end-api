package mementee.mementee.api.controller.memberDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginMemberResponse {
    private Long memberId;
    private String accessToken;
    private String refreshToken;
    //private String name;        //사용자 이름
    private String message;


}
