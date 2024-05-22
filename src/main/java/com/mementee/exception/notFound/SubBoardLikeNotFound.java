package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class SubBoardLikeNotFound extends NotFoundException{
    public SubBoardLikeNotFound() {
        super(ErrorCode.LIKE_SUB_BOARD_NOT_FOUND);
    }
}
