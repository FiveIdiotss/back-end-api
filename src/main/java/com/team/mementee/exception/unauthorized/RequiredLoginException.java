package com.team.mementee.exception.unauthorized;

import com.team.mementee.config.error.ErrorCode;

public class RequiredLoginException extends UnauthorizedException{

    public RequiredLoginException() {
        super(ErrorCode.REQUIRED_LOGIN);
    }
}
