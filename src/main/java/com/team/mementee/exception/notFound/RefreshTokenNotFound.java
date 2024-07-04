package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class RefreshTokenNotFound extends NotFoundException{

    public RefreshTokenNotFound() {
        super(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
    }
}
