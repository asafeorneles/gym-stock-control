package com.asafeorneles.gymstock.exceptions;

public class InsufficientProductQuantityException extends RuntimeException {
    public InsufficientProductQuantityException(String message) {
        super(message);
    }
}
