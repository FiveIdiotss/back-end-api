package com.team.mementee.exception.conflict;

import com.team.mementee.config.error.ErrorCode;

public class MatchingConflictException extends ConflictException {
    public MatchingConflictException() {
        super(ErrorCode.MATCHING_CONFLICT);
    }
}
