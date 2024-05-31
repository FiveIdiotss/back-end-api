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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Member receiver;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matching_id")
    private Matching matching;

    private int unreadMessageCount;

    public ChatRoom(Member sender, Member receiver, Matching matching) {
        this.sender = sender;
        this.receiver = receiver;
        this.matching = matching;
    }
}
