package com.mementee.api.dto.chatDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class ChatUpdateDTO {

    private Long chatRoomId;
    private int unreadMessageCount;
    private LatestMessageDTO latestMessageDTO;

}
