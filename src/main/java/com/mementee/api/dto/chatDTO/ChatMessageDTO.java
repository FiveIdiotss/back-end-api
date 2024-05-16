package com.mementee.api.dto.chatDTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
    private String content;
    private String senderName;
    private Long senderId;
    private Long chatRoomId;
    private int readCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    private LocalDateTime localDateTime = LocalDateTime.now();

    public ChatMessageDTO(String content, String senderName, Long senderId, Long chatRoomId, LocalDateTime localDateTime) {
        this.content = content;
        this.senderName = senderName;
        this.senderId = senderId;
        this.chatRoomId = chatRoomId;
        this.localDateTime = localDateTime;
    }
}