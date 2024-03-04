package com.mementee.api.controller.chatDTO;

import lombok.Data;

@Data
public class ChatMessageDTO {
    private String content;
    private Long receiverId;
}
