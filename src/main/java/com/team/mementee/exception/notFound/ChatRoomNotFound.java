package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class ChatRoomNotFound extends NotFoundException {

    public ChatRoomNotFound() {
        super(ErrorCode.CHAT_ROOM_NOT_FOUND);
    }
}
