package com.asafeorneles.gymstock.dtos.auth;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        Long expiresIn
) {
}
