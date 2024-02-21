package com.mementee.vo.naver;

import lombok.Data;

@Data
public class NaverLoginResponse {
    // 결과 코드
    private String resultcode;

    // 사용자 프로필 조회 성공 여부
    private String message;

    // 사용자 정
    private NaverLoginRequest response;
}
