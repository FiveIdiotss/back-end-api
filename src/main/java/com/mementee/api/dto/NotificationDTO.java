package com.mementee.api.dto;

import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationDTO {
    private Long lastEventId;
    private Long receiverId;
    private ChatMessageDTO chatMessageDTO;
}
