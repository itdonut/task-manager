package com.example.task_manager.controllers.advice;

import com.example.task_manager.dtos.response.exception.ExceptionResponseDto;
import com.example.task_manager.exceptions.*;
import com.example.task_manager.utils.DateTimeUTC;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

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

    @ExceptionHandler(ResourceDeletionNotAllowedException.class)
    public ResponseEntity<?> handleResourceDeletionNotAllowedException(ResourceDeletionNotAllowedException e) {
        return createResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return createGeneralResponse(HttpStatus.METHOD_NOT_ALLOWED, "Request method not supported");
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Required request parameter is missing");
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(TypeMismatchException e) {
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Invalid parameter type");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        return createGeneralResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported content type");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(NoHandlerFoundException e) {
        return createGeneralResponse(HttpStatus.NOT_FOUND, "Path not found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return createGeneralResponse(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Invalid method argument type");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidBody(MethodArgumentNotValidException e) {
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Validation failed due to invalid request body");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleInvalidParam(ConstraintViolationException e) {
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Validation failed due to invalid request param");
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Validation failed due to binding error");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Unable to parse request body. Please check the JSON syntax and data types");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUniversalException(Exception e) {
        return createGeneralResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ExceptionResponseDto> createResponse(HttpStatus status, GeneralException e) {
        return ResponseEntity.status(status).body(
                ExceptionResponseDto.builder()
                        .message(e.getMessage())
                        .dateTime(e.getDateTime())
                        .build()
        );
    }

    private ResponseEntity<ExceptionResponseDto> createGeneralResponse(HttpStatus status, String message) {
        return createResponse(status, new GeneralException(message, DateTimeUTC.now()));
    }
}
