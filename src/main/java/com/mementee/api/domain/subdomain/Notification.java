package com.mementee.api.domain.subdomain;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @OneToOne
    @JoinColumn(name = "chat_message_id")
    private ChatMessage chatMessage;

    public Notification(Member receiver, ChatMessage chatMessage) {
        this.receiver = receiver;
        this.chatMessage = chatMessage;
    }
}
