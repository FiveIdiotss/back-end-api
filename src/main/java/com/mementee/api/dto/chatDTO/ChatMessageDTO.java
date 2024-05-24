package com.mementee.api.dto.chatDTO;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.mementee.api.domain.Member;
import com.mementee.api.domain.enumtype.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {

    private FileType fileType = FileType.MESSAGE; // 기본 설정: MESSAGE
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

//    public static ChatMessageDTO createChatMessageDTO(FileType fileType, ) {
//        return new ChatMessageDTO()
//    }

}