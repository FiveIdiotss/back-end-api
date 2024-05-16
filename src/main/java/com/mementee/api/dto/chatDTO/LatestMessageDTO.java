package com.mementee.api.dto.chatDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestMessageDTO {
    private String content;
    private LocalDateTime localDateTime;
}
