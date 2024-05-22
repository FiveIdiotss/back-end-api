package com.mementee.api.dto.chatDTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.chat.ChatMessage;
import com.mementee.api.domain.chat.ChatRoom;
import com.mementee.api.domain.enumtype.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private FileType fileType = FileType.MESSAGE;
    private String fileURL;
    private String content;
    private String senderName;
    private Long senderId;
    private Long chatRoomId;

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

    public static Slice<ChatMessageDTO> creatChatMessageDTO(Slice<ChatMessage> allMessages){
        return allMessages.map(message -> new ChatMessageDTO(
                message.getContent(),
                message.getSender().getName(),
                message.getSender().getId(),
                message.getChatRoom().getId(),
                message.getLocalDateTime()
        ));
    }

    public static ChatMessageDTO createFileChatMessageDTO(FileType fileType, String fileUrl, Member loginMember, Long chatRoomId){
        return new ChatMessageDTO(
                fileType,
                fileUrl,
                null,
                loginMember.getName(),
                loginMember.getId(),
                chatRoomId,
                LocalDateTime.now());
    }
}