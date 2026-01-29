package com.asafeorneles.gymstock.dtos.user;

import java.util.Set;
import java.util.UUID;

public record UserResponseDto(
        UUID userId,
        String username,
        Set<String> roles,
        String activityStatus
){
}
