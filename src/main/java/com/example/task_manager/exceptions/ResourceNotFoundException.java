package com.example.task_manager.exceptions;

import java.util.Date;

public class ResourceNotFoundException extends GeneralException {
    public ResourceNotFoundException(String message, Date date) {
        super(message, date);
    }
}
