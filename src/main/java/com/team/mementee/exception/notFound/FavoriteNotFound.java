package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class FavoriteNotFound extends NotFoundException{
    public FavoriteNotFound() {
        super(ErrorCode.FAVORITE_BOARD_NOT_FOUND);
    }
}
