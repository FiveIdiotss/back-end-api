package com.mementee.api.dto.chatDTO;

import com.mementee.api.domain.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatRoomDTO {

    private Long chatRoomId;
    private Long receiverId;
    private String receiverName;
    private LatestMessageDTO latestMessageDTO;

    public ChatRoomDTO(Long chatRoomId, Long receiverId, String receiverName) {
        this.chatRoomId = chatRoomId;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
    }
}
