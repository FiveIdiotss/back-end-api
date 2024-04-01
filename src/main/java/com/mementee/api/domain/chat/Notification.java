package com.mementee.api.domain.chat;

import com.mementee.api.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Notification {

    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public Notification(String message, Member member) {
        this.message = message;
        this.member = member;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "message='" + message + '\'' +
                ", member=" + member +
                '}';
    }
}
