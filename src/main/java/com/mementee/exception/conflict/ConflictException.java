package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;
import com.mementee.exception.BaseException;

public class ConflictException extends BaseException {

    public ConflictException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }
}
