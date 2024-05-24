package com.mementee.api.domain;

import com.mementee.api.domain.enumtype.BoardType;
import com.mementee.api.domain.enumtype.NotificationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FcmDetail {

    @Id
    @GeneratedValue
    @Column(name = "fcm_detail_id")
    private Long id;

    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sendMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiveMember;

    public FcmDetail(String content, NotificationType notificationType, Member sendMember, Member receiveMember) {
        this.content = content;
        this.notificationType = notificationType;
        this.sendMember =  sendMember;
        this.receiveMember = receiveMember;
    }
}
