package com.mementee.api.dto.notificationDTO;

import com.mementee.api.domain.Board;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.domain.enumtype.NotificationType;
import com.mementee.api.dto.applyDTO.ApplyRequest;
import com.mementee.api.dto.chatDTO.ChatMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FcmDTO {
    private Long targetMemberId;
    private String senderId;
    private String senderName;
    private String senderImageUrl;
    private String content;

    private NotificationType notificationType;
}
