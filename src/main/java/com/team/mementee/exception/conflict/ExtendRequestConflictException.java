package com.team.mementee.exception.conflict;

import com.team.mementee.config.error.ErrorCode;

public class ExtendRequestConflictException extends ConflictException {
    public ExtendRequestConflictException() {
        super(ErrorCode.EXTEND_REQUEST_CONFLICT);
    }
}
