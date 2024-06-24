package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class ChatMessageNotFound extends NotFoundException {
    public ChatMessageNotFound() {
        super(ErrorCode.CHAT_MESSAGE_NOT_FOUND);
    }
}
