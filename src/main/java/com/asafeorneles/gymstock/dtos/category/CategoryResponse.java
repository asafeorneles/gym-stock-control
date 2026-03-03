package com.asafeorneles.gymstock.dtos.category;

import java.util.UUID;

public record CategoryResponse(
        UUID categoryId,
        String name
) {
}
