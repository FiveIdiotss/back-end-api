package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class SchoolNotFound extends NotFoundException{
    public SchoolNotFound() {
        super(ErrorCode.SCHOOL_NOT_FOUND);
    }
}
