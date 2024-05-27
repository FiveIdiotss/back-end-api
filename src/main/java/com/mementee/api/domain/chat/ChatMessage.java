package com.mementee.api.domain.chat;

import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.FileType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    @Id
    @GeneratedValue
    @Column(name = "chat_message_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

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

    public ChatMessage(FileType fileType, String fileURL, String content, Member sender, ChatRoom chatRoom, LocalDateTime localDateTime) {
        this.fileType = fileType;
        this.fileURL = fileURL;
        this.content = content;
        this.sender = sender;
        this.chatRoom = chatRoom;
        this.localDateTime = localDateTime;
    }
}
