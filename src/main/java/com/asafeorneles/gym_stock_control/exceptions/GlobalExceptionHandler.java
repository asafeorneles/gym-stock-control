package com.asafeorneles.gym_stock_control.exceptions;

import com.asafeorneles.gym_stock_control.dtos.exception.ResponseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ResponseException> productNotFoundExceptionHandler(ProductNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseException(
                        404,
                        "NOT_FOUND",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ResponseException> categoryNotFoundExceptionHandler(CategoryNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseException(
                        404,
                        "NOT_FOUND",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(SaleNotFoundException.class)
    public ResponseEntity<ResponseException> saleNotFoundExceptionHandler(SaleNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseException(
                        404,
                        "NOT_FOUND",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(ProductInventoryNotFoundException.class)
    public ResponseEntity<ResponseException> productInventoryNotFoundExceptionHandler(ProductInventoryNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new ResponseException(
                        404,
                        "NOT_FOUND",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(InsufficientProductQuantityException.class)
    public ResponseEntity<ResponseException> insufficientProductQuantityExceptionHandler(InsufficientProductQuantityException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ResponseException(
                        409,
                        "CONFLICT",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    public ResponseEntity<ResponseException> productAlreadyExistsExceptionHandler(ProductAlreadyExistsException e){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ResponseException(
                        409,
                        "CONFLICT",
                        e.getMessage(),
                        LocalDateTime.now()
                )
        );
    }

}
