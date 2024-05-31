package com.mementee.api.dto.notificationDTO;

import com.mementee.api.domain.Notification;
import com.mementee.api.domain.enumtype.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationDTO {
    private Long notificationId;

    private Long senderId;
    private String senderName;
    private String senderImageUrl;

    private Long otherPK;           //Notification Kind PK
    private String title;       //title
    private String content;

    private NotificationType notificationType;

    private LocalDateTime arriveTime;

    public static NotificationDTO createNotificationDTO(Notification notification){
        return new NotificationDTO(notification.getId(), notification.getSendMember().getId(), notification.getSendMember().getName(), notification.getSendMember().getMemberImageUrl(),
                notification.getOtherPK(), notification.getTitle(), notification.getContent(), notification.getType(), notification.getArriveTime());
    }

    public static List<NotificationDTO> createNotificationDTOs(List<Notification> notifications){
        return notifications.stream().map(NotificationDTO::createNotificationDTO)
                .toList();
    }

}
