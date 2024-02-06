package mementee.mementee.api.controller.boardDTO;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@Data
public class BoardInfoResponse {
    private BoardDTO boardDTO;

    private LocalTime startTime;            // 예약 가능한 시작 시간
    private LocalTime lastTime;              // 예약 가능한 종료 시간
    private List<DayOfWeek> availableDays;  //상담 가능한 요일
}
