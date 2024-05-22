package com.mementee.api.dto.matchingDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Data
public class MatchingDTO {
    private Long matchingId;
    private Long applyId;

    private Long otherMemberId;
    private String otherMemberName;

    private LocalDate date;                     // 상담 날짜
    private LocalTime startTime;                // 상담 시작 시간


}
