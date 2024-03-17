package com.mementee.api.controller.chatDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String content;
    private Long senderId;
    private Long chatRoomId;
    private LocalDateTime localDateTime = LocalDateTime.now();
}
