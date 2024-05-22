package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class MajorNotFound extends NotFoundException {

    public MajorNotFound() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
