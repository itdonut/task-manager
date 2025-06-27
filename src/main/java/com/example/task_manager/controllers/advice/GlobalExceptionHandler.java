package com.example.task_manager.controllers.advice;

import com.example.task_manager.dtos.response.exception.ExceptionResponseDto;
import com.example.task_manager.exceptions.*;
import com.example.task_manager.utils.DateTimeUTC;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        logger.error("Resource already exists", e);
        return createResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        logger.error("Resource is not found", e);
        return createResponse(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(UserIsNotTeamMemberException.class)
    public ResponseEntity<?> handleUserIsNotTeamMemberException(UserIsNotTeamMemberException e) {
        logger.error("User is not a member of team", e);
        return createResponse(HttpStatus.FORBIDDEN, e);
    }

    @ExceptionHandler(ResourceDeletionNotAllowedException.class)
    public ResponseEntity<?> handleResourceDeletionNotAllowedException(ResourceDeletionNotAllowedException e) {
        logger.error("Resource deletion is not allowed", e);
        return createResponse(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.error("Request method not supported", e);
        return createGeneralResponse(HttpStatus.METHOD_NOT_ALLOWED, "Request method not supported");
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error("Required request parameter is missing", e);
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Required request parameter is missing");
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(TypeMismatchException e) {
        logger.error("Invalid parameter type", e);
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Invalid parameter type");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException e) {
        logger.error("No static resource", e);
        return createGeneralResponse(HttpStatus.NOT_FOUND, "No static resource");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException e) {
        logger.error("Unsupported content type", e);
        return createGeneralResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported content type");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleHttpMediaTypeNotSupportedException(NoHandlerFoundException e) {
        logger.error("Path not found", e);
        return createGeneralResponse(HttpStatus.NOT_FOUND, "Path not found");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        logger.error("Access denied", e);
        return createGeneralResponse(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        logger.error("Invalid method argument type", e);
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Invalid method argument type");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidBody(MethodArgumentNotValidException e) {
        logger.error("Invalid request body", e);
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Validation failed due to invalid request body");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleInvalidParam(ConstraintViolationException e) {
        logger.error("Invalid request param", e);
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Validation failed due to invalid request param");
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleBindException(BindException e) {
        logger.error("Binding error", e);
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Validation failed due to binding error");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("Invalid JSON", e);
        return createGeneralResponse(HttpStatus.BAD_REQUEST, "Unable to parse request body. Please check the JSON syntax and data types");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUniversalException(Exception e) {
        logger.error("Internal server error", e);
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
