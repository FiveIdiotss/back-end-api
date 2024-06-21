package com.team.mementee.exception.conflict;

import com.team.mementee.config.error.ErrorCode;

public class EmailConflictException extends ConflictException {
    public EmailConflictException() {
        super(ErrorCode.EMAIL_CONFLICT);
    }
}
