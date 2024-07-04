package com.team.mementee.exception.conflict;

import com.team.mementee.config.error.ErrorCode;

public class FavoriteConflictException extends ConflictException{

    public FavoriteConflictException() {
        super(ErrorCode.FAVORITE_BOARD_CONFLICT);
    }
}
