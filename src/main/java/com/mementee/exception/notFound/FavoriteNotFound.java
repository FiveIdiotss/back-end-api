package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class FavoriteNotFound extends NotFoundException{
    public FavoriteNotFound() {
        super(ErrorCode.FAVORITE_BOARD_NOT_FOUND);
    }
}
