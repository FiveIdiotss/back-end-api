package mementee.mementee.vo;

import lombok.Data;

@Data
public class SocialToken {

    // 접근 토큰
    private String access_token;

    // 갱신 토큰
    private String refresh_type;

    // 토큰 타입
    private String token_type;

    // 토큰 유효 기간
    private int expires_in;

    // 에러 코드
    private String error;

    // 에러 메시지
    private String error_description;

    private String scope;
    private String id_token;
}