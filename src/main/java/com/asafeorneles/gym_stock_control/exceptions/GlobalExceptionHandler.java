package com.asafeorneles.gym_stock_control.exceptions;

import com.asafeorneles.gym_stock_control.dtos.exception.ResponseException;
import com.asafeorneles.gym_stock_control.dtos.exception.ResponseExceptionValidation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseException> resourceNotFoundExceptionHandler(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseException(
                        HttpStatus.NOT_FOUND.value(),
                        "NOT_FOUND",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(StatusActivityException.class)
    public ResponseEntity<ResponseException> statusActivityExceptionHandler(StatusActivityException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ResponseException(
                        HttpStatus.CONFLICT.value(),
                        "CONFLICT",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(BusinessConflictException.class)
    public ResponseEntity<ResponseException> businessConflictExceptionHandler(BusinessConflictException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ResponseException(
                        HttpStatus.CONFLICT.value(),
                        "CONFLICT",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(InsufficientProductQuantityException.class)
    public ResponseEntity<ResponseException> insufficientProductQuantityExceptionHandler(InsufficientProductQuantityException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ResponseException(
                        HttpStatus.CONFLICT.value(),
                        "CONFLICT",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(InvalidCouponException.class)
    public ResponseEntity<ResponseException> invalidCouponExceptionHandler(InvalidCouponException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseException(
                        HttpStatus.BAD_REQUEST.value(),
                        "BAD_REQUEST",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();

        e.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseExceptionValidation(
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                "Validation error in the submitted fields.",
                LocalDateTime.now(),
                fieldErrors
        ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseExceptionValidation> handleConstraintViolation(ConstraintViolationException e) {

        Map<String, String> fieldErrors = new HashMap<>();

        e.getConstraintViolations().forEach(violation ->
                fieldErrors.put(violation.getPropertyPath().toString(), violation.getMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseExceptionValidation(
                        HttpStatus.BAD_REQUEST.value(),
                        "BAD_REQUEST",
                        "Validation error in the submitted fields.",
                        LocalDateTime.now(),
                        fieldErrors
                )
        );
    }

}
