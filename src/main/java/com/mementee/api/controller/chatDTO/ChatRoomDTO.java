package com.mementee.api.controller.chatDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatRoomDTO {

    private Long chatRoomId;
    private Long receiverId;
    private String receiverName;
    private LocalDateTime latestMessageSentTime;

    public ChatRoomDTO(Long chatRoomId, Long receiverId, String receiverName) {
        this.chatRoomId = chatRoomId;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
    }
}
