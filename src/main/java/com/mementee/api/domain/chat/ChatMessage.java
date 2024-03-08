package com.mementee.api.domain.chat;

import com.mementee.api.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
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
    @CreatedDate
    private LocalDateTime localDateTime;


    public ChatMessage(String content, Member sender, ChatRoom chatRoom, LocalDateTime localDateTime) {
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.localDateTime = localDateTime;
    }

}
