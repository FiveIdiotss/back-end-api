package mementee.mementee.vo.naver;

import lombok.Data;

@Data
public class NaverLoginProfile {

    // 네이버 고유 아이디, 동일인 식별
    private String id;

    // 사용자 이름
    private String name;

    // 사용자 메일 주소
    private String email;

    // 성별
    private String gender;

    // 휴대전화번호
    private String mobile;
}
