package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;
import com.mementee.exception.BaseException;

public class NotFoundException extends BaseException {

    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public NotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}
