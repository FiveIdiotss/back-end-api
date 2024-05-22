package com.mementee.exception.conflict;

import com.mementee.config.error.ErrorCode;

public class SubBoardLikeConflictException extends ConflictException{

    public SubBoardLikeConflictException() {
        super(ErrorCode.LIKE_SUB_BOARD_CONFLICT);
    }
}
