package com.asafeorneles.gymstock.dtos.exception;

import java.time.LocalDateTime;

public record ExceptionResponse(
        int code,
        String error,
        String message,
        LocalDateTime timestamp
){
}
