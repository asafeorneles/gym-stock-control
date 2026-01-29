package com.asafeorneles.gymstock.dtos.exception;

import java.time.LocalDateTime;

public record ResponseException (
        int code,
        String error,
        String message,
        LocalDateTime timestamp
){
}
