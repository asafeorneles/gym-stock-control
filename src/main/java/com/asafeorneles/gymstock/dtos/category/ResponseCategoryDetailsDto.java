package com.asafeorneles.gymstock.dtos.category;

import com.asafeorneles.gymstock.enums.ActivityStatus;

import java.util.UUID;

public record ResponseCategoryDetailsDto(
        UUID categoryId,
        String name,
        String description,
        ActivityStatus activityStatus
) {
}
