package com.team.mementee.api.domain.chat;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.enumtype.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private MessageType messageType;

    private String fileURL;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    private int readCount = 1;

    private LocalDateTime localDateTime;

    public ChatMessage(MessageType messageType, String fileURL, String content, Member sender, ChatRoom chatRoom, LocalDateTime localDateTime) {
        this.messageType = messageType;
        this.fileURL = fileURL;
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.localDateTime = localDateTime;
    }
}
