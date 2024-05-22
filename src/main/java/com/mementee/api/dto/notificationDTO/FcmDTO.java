package com.mementee.api.dto.notificationDTO;

import com.mementee.api.domain.FcmDetail;
import com.mementee.api.domain.enumtype.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    public static List<FcmDTO> createFcmList(List<FcmDetail> fds){
        return fds.stream()
                .map(fd -> new FcmDTO(fd.getReceiveMember().getId(),
                        String.valueOf(fd.getSendMember().getId()),
                        fd.getSendMember().getName(),
                        fd.getSendMember().getMemberImageUrl(),
                        fd.getContent(),
                        fd.getNotificationType()))
                .toList();
    }
}
