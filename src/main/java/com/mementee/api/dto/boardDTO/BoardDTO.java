package com.mementee.api.dto.boardDTO;

import com.mementee.api.domain.enumtype.BoardCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.mementee.api.domain.enumtype.BoardType;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BoardDTO {
    private Long boardId;
    private BoardCategory boardCategory;
    private BoardType boardType;

    private String title;
    private String introduce;
    private String target;
    private String content;

    private int year;           //작성자 학번
    private String schoolName;  //작성자 학교
    private String majorName;   //작성자 전공

    private Long memberId;      //작성자
    private String memberName;  //작성자

    private LocalDateTime writeTime;  ////작성 시간
}
