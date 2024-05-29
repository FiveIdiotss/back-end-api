package com.mementee.api.dto.chatDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ChatRoomUpdateDTO {

    private Long chatRoomId;
    private Long receiverId;
    private int unreadMessageCount;

}
