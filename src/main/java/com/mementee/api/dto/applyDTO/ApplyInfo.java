package com.mementee.api.dto.applyDTO;

import com.mementee.api.domain.enumtype.ApplyState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Data
public class ApplyInfo {
    private Long applyId;
    //private BoardDTO boardDTO;
    private Long boardId;
    private String boardTitle;

    private String content;

    private ApplyState applyState;

    private LocalDate date;
    private LocalTime startTime;

    private Long memberId;
    private String memberName;
    private String memberImageUrl;
    private String schoolName;
    private String majorName;
}
