package com.asafeorneles.gymstock.dtos.auth;

public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
