package mementee.mementee.api.domain.subdomain;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.time.LocalTime;

@Embeddable
@Data
public class ScheduleTime {
    private LocalTime startTime;
    private LocalTime endTime;
}
