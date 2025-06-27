package com.example.task_manager.controllers.advice;

import com.example.task_manager.dtos.response.exception.ExceptionResponseDto;
import com.example.task_manager.exceptions.GeneralException;
import com.example.task_manager.exceptions.ResourceAlreadyExistsException;
import com.example.task_manager.exceptions.ResourceNotFoundException;
import com.example.task_manager.exceptions.UserIsNotTeamMemberException;
import com.example.task_manager.utils.DateTimeUTC;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        return createResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        return createResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(UserIsNotTeamMemberException.class)
    public ResponseEntity<?> handleUserIsNotTeamMemberException(UserIsNotTeamMemberException e) {
        return createResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidBody(MethodArgumentNotValidException e) {
        return createValidationResponse("Validation failed due to invalid request body");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleInvalidParam(ConstraintViolationException e) {
        return createValidationResponse("Validation failed due to invalid request param");
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        return createValidationResponse("Validation failed due to binding error");
    }

    private ResponseEntity<ExceptionResponseDto> createResponse(HttpStatus status, GeneralException e) {
        return ResponseEntity.status(status).body(
                ExceptionResponseDto.builder()
                        .message(e.getMessage())
                        .dateTime(e.getDateTime())
                        .build()
        );
    }

    private ResponseEntity<ExceptionResponseDto> createValidationResponse(String message) {
        return createResponse(HttpStatus.BAD_REQUEST, new GeneralException(message, DateTimeUTC.now()));
    }
}
