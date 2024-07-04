package com.team.mementee.exception.conflict;

import com.team.mementee.config.error.ErrorCode;
import com.team.mementee.exception.BaseException;

public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }
}
