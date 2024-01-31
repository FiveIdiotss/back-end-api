package mementee.mementee.api.domain.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class ChatMessage {

    @Id @GeneratedValue
    private Long id;

    private String sender;
    private String receiver;
    private LocalDateTime localDateTime;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;
}
