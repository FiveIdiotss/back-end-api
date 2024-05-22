package com.mementee.exception.unauthorized;

import com.mementee.config.error.ErrorCode;
import com.mementee.exception.BaseException;

public class InvalidTokenException extends UnauthorizedException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
