package com.mementee.exception.unauthorized;

import com.mementee.config.error.ErrorCode;
import com.mementee.exception.BaseException;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
