package com.mementee.api.dto.chatDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class LatestMessageDTO {
    private String content;
    private LocalDateTime localDateTime;
}
