package com.team.mementee.exception.conflict;

import com.team.mementee.config.error.ErrorCode;

public class MyApplyConflictException extends ConflictException{
    public MyApplyConflictException() {
        super(ErrorCode.MY_APPLY_BOARD_CONFLICT);
    }
}
