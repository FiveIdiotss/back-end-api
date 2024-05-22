package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;

public class ApplyConflictException extends ConflictException {
    public ApplyConflictException() {
        super(ErrorCode.APPLY_BOARD_CONFLICT);
    }
}
