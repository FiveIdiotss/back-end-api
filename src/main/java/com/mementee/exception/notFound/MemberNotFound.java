package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class MemberNotFound extends NotFoundException{
    public MemberNotFound() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
