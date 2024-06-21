package com.team.mementee.exception.notFound;

import com.team.mementee.config.error.ErrorCode;

public class NotificationNotFound extends NotFoundException {

    public NotificationNotFound() {
        super(ErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
