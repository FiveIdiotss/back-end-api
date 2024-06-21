package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class BoardNotFound extends NotFoundException {
    public BoardNotFound() {
        super(ErrorCode.BOARD_NOT_FOUND);
    }
}
