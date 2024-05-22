package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class RefreshTokenNotFound extends NotFoundException{

    public RefreshTokenNotFound() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}
