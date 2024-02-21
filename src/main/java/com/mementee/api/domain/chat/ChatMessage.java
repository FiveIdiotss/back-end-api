package com.mementee.api.domain.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class ChatMessage {

    @Id @GeneratedValue
    private Long id;

    private String sender;
    private String receiver;
    private LocalDateTime localDateTime;

    @ManyToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
}
