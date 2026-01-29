package com.asafeorneles.gymstock.dtos.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ResponseExceptionValidation(
        int code,
        String error,
        String message,
        LocalDateTime timestamp,
        Map<String, String> fieldErrors
){
}
