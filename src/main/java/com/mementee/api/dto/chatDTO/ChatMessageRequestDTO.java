package com.mementee.api.dto.chatDTO;

import lombok.Data;

@Data
public class ChatMessageRequestDTO {
    private Long chatRoomId;
    private int page;
    private int size;
}
