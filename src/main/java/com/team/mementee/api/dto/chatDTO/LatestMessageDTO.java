package com.team.mementee.api.dto.chatDTO;

import com.team.mementee.api.domain.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LatestMessageDTO {
    private String content;
    private LocalDateTime localDateTime;

    public static LatestMessageDTO createLatestMessageDTO(Optional<ChatMessage> latestChatMessage){
        return latestChatMessage.map(chatMessage ->
                        new LatestMessageDTO(chatMessage.getContent(), chatMessage.getLocalDateTime()))
                .orElse(new LatestMessageDTO(" ", null));
    }
}
