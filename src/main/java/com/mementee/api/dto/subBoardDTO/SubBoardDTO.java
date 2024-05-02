package com.mementee.api.dto.subBoardDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SubBoardDTO {
    private Long subBoardId;

    private String title;
    private String content;

    private int year;           //작성자 학번
    private String schoolName;  //작성자 학교
    private String majorName;   //작성자 전공

    private Long memberId;      //작성자
    private String memberName;  //작성자

    private LocalDateTime writeTime;  ////작성 시간
}
