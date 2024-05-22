package com.mementee.api.dto.applyDTO;

import com.mementee.api.domain.Apply;
import com.mementee.api.domain.enumtype.ApplyState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Data
public class ApplyInfoResponse {
    private Long applyId;

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

    public static ApplyInfoResponse createApplyInfo(Apply apply){
        return new ApplyInfoResponse(apply.getId(), apply.getBoard().getId(),
                apply.getContent(), apply.getBoard().getTitle(), apply.getApplyState(),
                apply.getDate(), apply.getStartTime(), apply.getBoard().getMember().getId(),
                apply.getBoard().getMember().getName(), apply.getBoard().getMember().getMemberImageUrl()
                ,apply.getBoard().getMember().getMajor().getSchool().getName(),
                apply.getBoard().getMember().getMajor().getName());
    }
}
