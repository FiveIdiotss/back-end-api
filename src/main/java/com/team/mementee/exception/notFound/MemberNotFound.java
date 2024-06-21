package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;
public class MemberNotFound extends NotFoundException {
    public MemberNotFound() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}
