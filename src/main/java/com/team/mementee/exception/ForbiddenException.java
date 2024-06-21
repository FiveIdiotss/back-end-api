package com.team.mementee.exception;

import com.team.mementee.config.error.ErrorCode;

public class ForbiddenException extends BaseException {

    public ForbiddenException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}
