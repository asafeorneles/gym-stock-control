package com.asafeorneles.gym_stock_control.exceptions;

public class SaleNotFoundException extends RuntimeException{
    public SaleNotFoundException(String message){
        super(message);
    }
}
