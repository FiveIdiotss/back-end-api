package com.mementee.api.dto.applyDTO;

import com.mementee.api.domain.enumtype.ApplyState;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ReceiveApplyDTO {
    private Long applyId;
    //private BoardDTO boardDTO;
    private Long boardId;
    private String boardTitle;

    private String content;

    private ApplyState applyState;

    private Long sendMemberId;
    private String sendMemberName;

    private LocalDate date;
    private LocalTime startTime;
}
