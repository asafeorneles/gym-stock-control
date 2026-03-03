package com.asafeorneles.gymstock.dtos.product;

import com.asafeorneles.gymstock.dtos.category.CategoryResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String name,
        String brand,
        String description,
        BigDecimal price,
        CategoryResponse category
) {
}
