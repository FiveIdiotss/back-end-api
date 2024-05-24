package com.mementee.api.domain.chat;

import com.mementee.api.domain.Matching;
import com.mementee.api.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue
    @Column(name = "chat_room_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id")
    private Matching matching;

    public ChatRoom(Member sender, Member receiver, Matching matching) {
        this.sender = sender;
        this.receiver = receiver;
        this.matching = matching;
    }
}
