package com.team.mementee.exception.conflict;

import com.team.mementee.config.error.ErrorCode;

public class ApplyConflictException extends ConflictException {
    public ApplyConflictException() {
        super(ErrorCode.APPLY_BOARD_CONFLICT);
    }
}
