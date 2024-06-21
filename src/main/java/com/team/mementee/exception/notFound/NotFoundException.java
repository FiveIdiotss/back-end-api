package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;
import com.team.mementee.exception.BaseException;

public class NotFoundException extends BaseException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}
