package com.cict.core.exception;

import com.cict.core.base.Loggable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.ZoneId;

@ControllerAdvice
public class ErrorHandlingControllerAdvice extends Loggable {
    @Value("${application.locale}")
    private String locale;

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onConstraintValidationException(HttpServletRequest request, ConstraintViolationException e) {
        log.error("ConstraintValidationException: {}", e.getMessage());
        e.printStackTrace();
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setTimestamp(LocalDateTime.now(ZoneId.of(locale)));
        error.setPath(request.getServletPath());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        for (ConstraintViolation violation : e.getConstraintViolations()) {
            error.getErrors().add(
                new Violation(violation.getPropertyPath().toString(), violation.getMessage(), violation.getInvalidValue()));
        }

        error.processMessages();
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ValidationErrorResponse onMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        e.printStackTrace();
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setTimestamp(LocalDateTime.now(ZoneId.of(locale)));
        error.setPath(request.getServletPath());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            error.getErrors().add(
                new Violation(fieldError.getField(), fieldError.getDefaultMessage()));
        }

        error.processMessages();
        return error;
    }

    @ExceptionHandler(RestException.class)
//    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    ValidationErrorResponse onRestException(HttpServletRequest request, RestException e) {
        log.error("RestException: {}", e.getMessage());
        e.printStackTrace();
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setTimestamp(LocalDateTime.now(ZoneId.of(locale)));
        error.setPath(request.getServletPath());
        error.setStatus(e.getStatus());
        error.setError(HttpStatus.valueOf(e.getStatus()).getReasonPhrase());
        error.setMessage(e.getMessage());
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    ValidationErrorResponse onException(HttpServletRequest request, Exception e) {
        log.error("Exception: {}", e.getMessage());
        e.printStackTrace();
        ValidationErrorResponse error = new ValidationErrorResponse();
        error.setTimestamp(LocalDateTime.now(ZoneId.of(locale)));
        error.setPath(request.getServletPath());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        error.setMessage(e.getMessage());
        return error;
    }
}