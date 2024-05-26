package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class FileNotFound extends NotFoundException{
    public FileNotFound() {
        super(ErrorCode.FILE_NOT_FOUND);
    }
}
