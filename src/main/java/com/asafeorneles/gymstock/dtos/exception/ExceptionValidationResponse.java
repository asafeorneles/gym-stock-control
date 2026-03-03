package com.asafeorneles.gymstock.dtos.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ExceptionValidationResponse(
        int code,
        String error,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
){
}
