package com.asafeorneles.gym_stock_control.exceptions;

public class CouponNotFoundException extends RuntimeException{
    public CouponNotFoundException(String message){
        super(message);
    }
}
