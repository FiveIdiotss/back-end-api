package com.mementee.api.dto.boardDTO;

import com.mementee.api.domain.subdomain.ScheduleTime;
import com.mementee.api.domain.subdomain.UnavailableTime;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.List;

@AllArgsConstructor
@Data
public class BoardInfoResponse {
    private BoardDTO boardDTO;

    private int consultTime;                          //상담 시간
    private List<ScheduleTime> times;                 //예약 가능 시간
    private List<DayOfWeek> availableDays;            //상감 가능한 요일

    private List<UnavailableTime> unavailableTimes;   //예약 불가한 시간
    private List<BoardImageDTO> boardImageUrls;       //게시물에 등록된 이미지

    //private List<LocalDate> availableDays;          //상담 가능한 요일
}
