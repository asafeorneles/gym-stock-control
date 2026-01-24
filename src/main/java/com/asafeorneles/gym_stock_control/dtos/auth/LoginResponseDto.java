package com.asafeorneles.gym_stock_control.dtos.auth;

public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
