package com.asafeorneles.gym_stock_control.exceptions;

import java.time.LocalDateTime;

public record ResponseException (
        int code,
        String error,
        String message,
        LocalDateTime timestamp
){
}
