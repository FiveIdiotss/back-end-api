package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;

public class ExtendResponseConflictException extends ConflictException {
    public ExtendResponseConflictException() {
        super(ErrorCode.EXTEND_RESPONSE_CONFLICT);
    }
}
