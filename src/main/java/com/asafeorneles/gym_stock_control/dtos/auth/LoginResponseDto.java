package com.asafeorneles.gym_stock_control.dtos.auth;

public record LoginResponseDto(String token, Long expiresIn) {
}
