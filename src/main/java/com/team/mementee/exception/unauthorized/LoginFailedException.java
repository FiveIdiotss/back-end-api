package com.team.mementee.exception.unauthorized;

import com.team.mementee.config.error.ErrorCode;

public class LoginFailedException extends UnauthorizedException {
    public LoginFailedException() {
        super(ErrorCode.LOGIN_FAILED);
    }
}
