package com.team.mementee.exception.unauthorized;

import com.team.mementee.config.error.ErrorCode;
import com.team.mementee.exception.BaseException;

public class UnauthorizedException extends BaseException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}
