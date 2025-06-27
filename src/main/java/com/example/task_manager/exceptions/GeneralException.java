package com.example.task_manager.exceptions;

import lombok.Getter;

import java.util.Date;

@Getter
public class GeneralException extends RuntimeException {
    private final Date dateTime;

    public GeneralException(String message, Date dateTime) {
        super(message);
        this.dateTime = dateTime;
    }

}
