package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class ApplyNotFound extends NotFoundException {

    public ApplyNotFound() {
        super(ErrorCode.APPLY_NOT_FOUND);
    }
}
