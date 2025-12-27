package com.asafeorneles.gym_stock_control.exceptions;

public class CouponAlreadyActiveException extends RuntimeException {
    public CouponAlreadyActiveException(String message) {
        super(message);
    }
}
