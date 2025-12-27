package com.asafeorneles.gym_stock_control.exceptions;

public class CategoryAlreadyUsedException extends RuntimeException {
    public CategoryAlreadyUsedException(String message) {
        super(message);
    }
}
