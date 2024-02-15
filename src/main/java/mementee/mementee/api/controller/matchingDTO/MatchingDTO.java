package mementee.mementee.api.controller.matchingDTO;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@Data
public class MatchingDTO {
    private Long matchingId;

    private LocalDate date;                     // 상담 날짜
    private LocalTime startTime;                // 상담 시작 시간

    //private String mentorId;
}
