package com.team.mementee.api.dto.notificationDTO;

import com.team.mementee.api.domain.enumtype.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FcmDTO {
    private Long targetMemberId;

    private Long senderId;
    private String senderName;
    private String senderImageUrl;

    private String title;
    private String content;

    private Long otherPK;

    private NotificationType notificationType;
}
