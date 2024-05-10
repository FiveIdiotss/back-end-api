package com.mementee.api.domain.chat;

import com.mementee.api.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue
    @Column(name = "chat_message_id")
    private Long id;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private Member sender;

    @ManyToOne
    @JoinColumn(name = "chatRoom_id")
    private ChatRoom chatRoom;

    private String image;

    private LocalDateTime localDateTime;

    private int readCount;

    public ChatMessage(String content, Member sender, ChatRoom chatRoom, String image) {
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.image = image;
        this.localDateTime = LocalDateTime.now();
    }
}
