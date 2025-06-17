package com.example.task_manager.controllers.advice;

import com.example.task_manager.dtos.response.exception.ExceptionResponseDto;
import com.example.task_manager.exceptions.GeneralException;
import com.example.task_manager.exceptions.ResourceAlreadyExistsException;
import com.example.task_manager.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        return createResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceAlreadyExistsException e) {
        return createResponse(HttpStatus.NOT_FOUND, e);
    }

    private ResponseEntity<ExceptionResponseDto> createResponse(HttpStatus status, GeneralException e) {
        return ResponseEntity.status(status).body(
                ExceptionResponseDto.builder()
                        .message(e.getMessage())
                        .date(e.getDate())
                        .build()
        );
    }
}
