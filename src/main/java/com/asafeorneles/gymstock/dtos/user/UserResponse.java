package com.asafeorneles.gymstock.dtos.user;

import com.asafeorneles.gymstock.enums.ActivityStatus;

import java.util.Set;
import java.util.UUID;

public record UserResponse(
        UUID userId,
        String username,
        Set<String> roles,
        ActivityStatus activityStatus
){
}
