package mementee.mementee.api.domain.subdomain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnavailableTime {
    private LocalDate date;         //상담 날짜
    private LocalTime startTime;    //상담 시작 시간
}
