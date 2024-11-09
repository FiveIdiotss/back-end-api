package com.team.mementee.api.domain;

import com.team.mementee.api.domain.enumtype.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    private String title;
    private String content;

    @Column(nullable = false)
    private Long otherPK;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sendMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiveMember;

    private LocalDateTime arriveTime;            //작성 시간

    public Notification(String title, String content, Long otherPK, NotificationType type, Member sendMember, Member receiveMember) {
        this.title = title;
        this.content = content;
        this.otherPK = otherPK;
        this.type = type;
        this.sendMember =  sendMember;
        this.receiveMember = receiveMember;
        this.arriveTime = LocalDateTime.now();
    }
}
