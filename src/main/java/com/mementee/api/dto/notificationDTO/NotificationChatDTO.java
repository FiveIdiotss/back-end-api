package com.mementee.api.dto.notificationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationChatDTO {
    private String message;
    private String senderName;
}
