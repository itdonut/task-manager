package com.example.task_manager.exceptions;

import java.util.Date;

public class UserPasswordMismatchException extends GeneralException {
    public UserPasswordMismatchException(String message, Date date) {
        super(message, date);
    }
}
