package com.mementee.exception.notFound;

import com.mementee.config.error.ErrorCode;

public class NotificationNotFound extends NotFoundException {

    public NotificationNotFound() {
        super(ErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
