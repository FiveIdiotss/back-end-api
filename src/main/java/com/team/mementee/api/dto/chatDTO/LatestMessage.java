package com.team.mementee.api.dto.chatDTO;

import com.team.mementee.api.domain.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LatestMessage {

    private String content;
    private LocalDateTime localDateTime;

    public static LatestMessage of(ChatMessage latestChatMessage) {
        if (latestChatMessage == null) return new LatestMessage("", null);
        return new LatestMessage(latestChatMessage.getContent(), latestChatMessage.getLocalDateTime());
    }
}
