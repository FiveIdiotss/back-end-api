package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class FcmNotification {

    @Id
    @GeneratedValue
    @Column(name = "fcm_notification_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String token;

    public FcmNotification(String token, Member member) {
        this.token = token;
        this.member = member;
    }

    public void updateFCMToken(String updateToken){
        this.token = updateToken;
    }
}
