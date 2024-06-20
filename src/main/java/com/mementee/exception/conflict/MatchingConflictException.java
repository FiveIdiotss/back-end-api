package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;

public class MatchingConflictException extends ConflictException {
    public MatchingConflictException() {
        super(ErrorCode.MATCHING_CONFLICT);
    }
}
