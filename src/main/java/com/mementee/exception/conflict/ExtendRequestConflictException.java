package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;

public class ExtendRequestConflictException extends ConflictException {
    public ExtendRequestConflictException() {
        super(ErrorCode.EXTEND_REQUEST_CONFLICT);
    }
}
