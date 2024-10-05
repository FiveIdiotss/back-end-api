package com.team.mementee.api.domain.chat;

import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.enumtype.MessageType;
import com.team.mementee.api.dto.chatDTO.ChatMessageRequest;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
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

    private int readCount;

    private LocalDateTime localDateTime;

    public ChatMessage(ChatMessageRequest request, Member sender, ChatRoom chatRoom) {
        this.messageType = request.getMessageType();
        this.fileURL = request.getFileURL();
        this.content = request.getContent();
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.localDateTime = request.getLocalDateTime();
        this.readCount = request.getReadCount();
    }

    public void changeToComplete(){
        this.messageType = MessageType.CONSULT_EXTEND_COMPLETE;
    }
}
