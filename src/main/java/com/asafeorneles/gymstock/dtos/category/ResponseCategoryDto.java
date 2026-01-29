package com.asafeorneles.gymstock.dtos.category;

import java.util.UUID;

public record ResponseCategoryDto(
        UUID categoryId,
        String name
) {
}
