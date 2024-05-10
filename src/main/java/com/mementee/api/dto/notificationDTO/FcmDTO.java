package com.mementee.api.dto.notificationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FcmDTO {
    private Long targetMemberId;
    private String senderName;
    private String content;
    private String senderId;
    private String senderImageUrl;
}
