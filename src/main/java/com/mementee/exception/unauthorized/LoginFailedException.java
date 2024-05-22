package com.mementee.exception.unauthorized;

import com.mementee.config.error.ErrorCode;
import com.mementee.exception.BaseException;

public class LoginFailedException extends UnauthorizedException {
    public LoginFailedException() {
        super(ErrorCode.LOGIN_FAILED);
    }
}
