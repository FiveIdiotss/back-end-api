package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class HeaderNotFound extends NotFoundException {

    public HeaderNotFound() {
        super(ErrorCode.HEADER_NOT_FOUND);
    }
}
