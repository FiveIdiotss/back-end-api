package com.mementee.api.dto.chatDTO;

import com.mementee.api.domain.chat.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class ChatRoomDTO {

    private Long chatRoomId;
    private Long receiverId;
    private String receiverName;
    private LatestMessageDTO latestMessageDTO;

    private String receiverImageUrl;

    private String boardTitle;
    private LocalDate date;                     // 상담 날짜
    private LocalTime startTime;                // 상담 시작 시간

    public ChatRoomDTO(Long chatRoomId, Long receiverId, String receiverName) {
        this.chatRoomId = chatRoomId;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
    }
}
