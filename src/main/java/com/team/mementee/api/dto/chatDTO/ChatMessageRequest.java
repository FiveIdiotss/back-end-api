package com.team.mementee.api.dto.chatDTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.team.mementee.api.domain.Member;
import com.team.mementee.api.domain.enumtype.DecisionStatus;
import com.team.mementee.api.domain.enumtype.MessageType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ChatMessageRequest {

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT; // 기본 설정: MESSAGE
    private String fileURL;
    private String content;
    private String senderName;
    private Long senderId;
    private Long chatRoomId;

    @Builder.Default
    private int readCount = 1;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @CreatedDate
    @Builder.Default
    private LocalDateTime localDateTime = LocalDateTime.now();

    public void updateReadCount(Long userCount) {
        if (userCount == 2) this.readCount = 2;
    }

    public ChatMessageRequest updateChatRequest(Member member, Long chatRoomId) {
        this.senderName = member.getName();
        this.senderId = member.getId();
        this.chatRoomId = chatRoomId;
        return this;
    }

    public static ChatMessageRequest of(MessageType messageType, String fileURL, String fileName, Member loginMember, Long chatRoomId) {
        return com.team.mementee.api.dto.chatDTO.ChatMessageRequest.builder()
                .messageType(messageType)
                .fileURL(fileURL)
                .content(fileName)
                .senderName(loginMember.getName())
                .senderId(loginMember.getId())
                .chatRoomId(chatRoomId)
                .readCount(1)
                .localDateTime(LocalDateTime.now())
                .build();
    }

    public static ChatMessageRequest createExtendRequest(Member member, Long chatRoomId) {
        return new ChatMessageRequest(
                MessageType.CONSULT_EXTEND,
                null,
                "상담 연장을 요청하였습니다.",
                member.getName(),
                member.getId(),
                chatRoomId,
                1,
                LocalDateTime.now());
    }

    public static ChatMessageRequest createUserEnterChatRequest(Member member, Long chatRoomId) {
        return new ChatMessageRequest(
                MessageType.USER_ENTER,
                null,
                "유저가 채팅방에 입장하였습니다.",
                member.getName(),
                member.getId(),
                chatRoomId,
                1,
                LocalDateTime.now());
    }

    public static ChatMessageRequest createUserLeaveChatRequest(Member member, Long chatRoomId) {
        return new ChatMessageRequest(
                MessageType.USER_LEAVE,
                null,
                "유저가 채팅방에서 퇴장하였습니다.",
                member.getName(),
                member.getId(),
                chatRoomId,
                1,
                LocalDateTime.now());
    }

    public static ChatMessageRequest createExtendResponse(DecisionStatus decisionStatus, Member member, Long chatRoomId) {
        if (decisionStatus.equals(DecisionStatus.ACCEPT))
            return new ChatMessageRequest(
                    MessageType.CONSULT_EXTEND_ACCEPT,
                    null,
                    "상담 연장을 수락하였습니다.",
                    member.getName(),
                    member.getId(),
                    chatRoomId,
                    1,
                    LocalDateTime.now());

        return new ChatMessageRequest(
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