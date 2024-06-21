package com.team.mementee.exception;

import com.team.mementee.config.error.ErrorCode;

public class ServerErrorException extends BaseException {

    public ServerErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public ServerErrorException() {
        super(ErrorCode.SERVER_ERROR);
    }
}
