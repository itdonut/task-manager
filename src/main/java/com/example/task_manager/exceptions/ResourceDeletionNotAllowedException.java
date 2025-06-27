package com.example.task_manager.exceptions;

import java.util.Date;

public class ResourceDeletionNotAllowedException extends GeneralException {
    public ResourceDeletionNotAllowedException(String message, Date dateTime) {
        super(message, dateTime);
    }
}
