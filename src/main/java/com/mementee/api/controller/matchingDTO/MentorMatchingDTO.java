package com.mementee.api.controller.matchingDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Data
public class MentorMatchingDTO {
    private Long matchingId;
    private Long applyId;
    private Long mentorMemberId;
    private String mentorMemberName;

    private LocalDate date;                     // 상담 날짜
    private LocalTime startTime;                // 상담 시작 시간

    //private String mentorId;
}
