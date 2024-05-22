package com.mementee.exception.unauthorized;

import com.mementee.config.error.ErrorCode;

public class RequiredLoginException extends UnauthorizedException{

    public RequiredLoginException() {
        super(ErrorCode.REQUIRED_LOGIN);
    }
}
