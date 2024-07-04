package com.team.mementee.api.dto.chatDTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.enumtype.MessageType;
import com.team.mementee.api.domain.enumtype.DecisionStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT; // 기본 설정: MESSAGE
    private String fileURL;
    private String content;
    private String senderName;
    private Long senderId;
    private Long chatRoomId;
    private int readCount = 1;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    private LocalDateTime localDateTime = LocalDateTime.now();

    public static ChatMessageDTO createExtendRequest(Member member, Long chatRoomId){
        return new ChatMessageDTO(
                MessageType.CONSULT_EXTEND,
                null,
                "상담 연장을 요청하였습니다.",
                member.getName(),
                member.getId(),
                chatRoomId,
                1,
                LocalDateTime.now());
    }

    public static ChatMessageDTO createExtendResponse(DecisionStatus decisionStatus, Member member, Long chatRoomId){
        if (decisionStatus.equals(DecisionStatus.ACCEPT))
             return new ChatMessageDTO(
                    MessageType.CONSULT_EXTEND_ACCEPT,
                    null,
                    "상담 연장을 수락하였습니다.",
                    member.getName(),
                    member.getId(),
                    chatRoomId,
                    1,
                    LocalDateTime.now());

        return new ChatMessageDTO(
                MessageType.CONSULT_EXTEND_DECLINE,
                null,
                "상담 연장을 거절하였습니다.",
                member.getName(),
                member.getId(),
                chatRoomId,
                1,
                LocalDateTime.now());
    }

}