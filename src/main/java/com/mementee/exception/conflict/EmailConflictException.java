package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;
import com.mementee.exception.BaseException;

public class EmailConflictException extends ConflictException {
    public EmailConflictException() {
        super(ErrorCode.EMAIL_CONFLICT);
    }
}
