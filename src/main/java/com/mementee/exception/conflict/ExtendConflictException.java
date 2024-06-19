package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;

public class ExtendConflictException extends ConflictException {
    public ExtendConflictException() {
        super(ErrorCode.EXTEND_CONFLICT);
    }
}
