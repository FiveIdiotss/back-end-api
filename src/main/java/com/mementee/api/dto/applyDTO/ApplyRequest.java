package com.mementee.api.dto.applyDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class ApplyRequest {
    private String content;

    private LocalDate date;       // 예약 날짜
    private LocalTime time;       // 예약 시간
}
