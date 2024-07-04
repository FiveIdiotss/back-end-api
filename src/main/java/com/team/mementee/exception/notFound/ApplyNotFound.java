package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class ApplyNotFound extends NotFoundException {

    public ApplyNotFound() {
        super(ErrorCode.APPLY_NOT_FOUND);
    }
}
