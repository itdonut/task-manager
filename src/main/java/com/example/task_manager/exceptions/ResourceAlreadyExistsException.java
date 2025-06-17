package com.example.task_manager.exceptions;

import java.util.Date;

public class ResourceAlreadyExistsException extends GeneralException {
    public ResourceAlreadyExistsException(String message, Date date) {
        super(message, date);
    }
}
