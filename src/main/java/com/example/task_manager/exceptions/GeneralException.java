package com.example.task_manager.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
public class GeneralException extends RuntimeException {
    private final Date date;

    public GeneralException(String message, Date date) {
        super(message);
        this.date = date;
    }

}
