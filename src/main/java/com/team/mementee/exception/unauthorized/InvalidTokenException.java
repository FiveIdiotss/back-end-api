package com.team.mementee.exception.unauthorized;

import com.team.mementee.config.error.ErrorCode;

public class InvalidTokenException extends UnauthorizedException {
    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}
