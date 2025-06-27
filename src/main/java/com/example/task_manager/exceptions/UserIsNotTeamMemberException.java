package com.example.task_manager.exceptions;

import java.util.Date;

public class UserIsNotTeamMemberException extends GeneralException {
    public UserIsNotTeamMemberException(String message, Date date) {
        super(message, date);
    }
}
