package com.mementee.api.domain.chat;

import com.mementee.api.domain.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue
    private Long chatRoomId;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    public void setSender(Member sender) {
        this.sender = sender;
    }

    public void setReceiver(Member receiver) {
        this.receiver = receiver;
    }

    public ChatRoom(Member sender, Member receiver) {
        this.sender = sender;
        this.receiver = receiver;
    }
}
