package com.team.mementee.api.dto.chatDTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.team.mementee.api.domain.chat.ChatMessage;
import com.team.mementee.api.domain.enumtype.MessageType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageDTO {

    private Long chatId;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
    private String fileURL;
    private String content;
    private String senderName;
    private Long senderId;
    private Long chatRoomId;
    private int readCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    private LocalDateTime localDateTime;

    public static ChatMessageDTO of(ChatMessage message) {
        return ChatMessageDTO.builder()
                .chatId(message.getId())
                .messageType(message.getMessageType())
                .fileURL(message.getFileURL())
                .content(message.getContent())
                .senderName(message.getSender().getName())
                .senderId(message.getSender().getId())
                .chatRoomId(message.getChatRoom().getId())
                .readCount(message.getReadCount())
                .build();
    }
}
