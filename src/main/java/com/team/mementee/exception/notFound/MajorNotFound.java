package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class MajorNotFound extends NotFoundException {

    public MajorNotFound() {
        super(ErrorCode.MAJOR_NOT_FOUND);
    }
}
