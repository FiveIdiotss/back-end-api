package com.mementee.api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Notification {
    @Id @GeneratedValue
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member receiver;

//    @OneToOne
//    @JoinColumn(name = "chat_message_id")
//    private ChatMessage chatMessage;


    private LocalDateTime createdAt;

    public Notification(Member receiver) {
        this.receiver = receiver;
        this.createdAt = LocalDateTime.now();
    }
}
