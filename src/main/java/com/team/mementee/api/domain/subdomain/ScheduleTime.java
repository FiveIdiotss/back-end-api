package com.team.mementee.api.domain.subdomain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleTime {
    private LocalTime startTime;
    private LocalTime endTime;
}
