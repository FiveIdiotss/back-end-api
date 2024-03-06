package com.mementee.api.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomDTO {

    private Long id;
    private String senderName;
    private String receiverName;
}
