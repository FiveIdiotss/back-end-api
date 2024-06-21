package com.team.mementee.exception.conflict;

import com.team.mementee.config.error.ErrorCode;

public class ProfileConflictException extends ConflictException {

    public ProfileConflictException() {
        super(ErrorCode.PROFILE_CONFLICT);
    }
}
