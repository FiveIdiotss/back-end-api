package com.team.mementee.api.domain.chat;

import com.team.mementee.api.domain.Matching;
import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.enumtype.ExtendState;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtendState extendState;

    public ChatRoom(Member sender, Member receiver, Matching matching) {
        this.sender = sender;
        this.receiver = receiver;
        this.matching = matching;
        this.extendState = ExtendState.EMPTY;
    }

    public void updateState(ChatRoom chatRoom) {
        if (chatRoom.getExtendState().equals(ExtendState.WAITING))
            chatRoom.extendState = ExtendState.EMPTY;
        else
            chatRoom.extendState = ExtendState.WAITING;
    }
}
