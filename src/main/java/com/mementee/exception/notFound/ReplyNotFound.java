package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class ReplyNotFound extends NotFoundException{
    public ReplyNotFound() {
        super(ErrorCode.REPLY_NOT_FOUND);
    }
}
