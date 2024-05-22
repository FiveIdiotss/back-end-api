package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;

public class ProfileConflictException extends ConflictException{

    public ProfileConflictException() {
        super(ErrorCode.PROFILE_CONFLICT);
    }
}
