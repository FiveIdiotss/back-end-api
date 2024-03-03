package com.mementee.api.domain.chat;

import com.mementee.api.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue
    private Long chatMessageId;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    @Column
    private LocalDateTime localDateTime;

    public void setSender(Member sender) {
        this.sender = sender;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public ChatMessage(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }

    public ChatMessage(String content, Member sender, ChatRoom chatRoom, LocalDateTime localDateTime) {
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.localDateTime = localDateTime;
    }
}
