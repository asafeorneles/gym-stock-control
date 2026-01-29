package com.asafeorneles.gymstock.dtos.product;

import com.asafeorneles.gymstock.dtos.category.ResponseCategoryDto;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseProductDto(
        UUID productId,
        String name,
        String brand,
        String description,
        BigDecimal price,
        ResponseCategoryDto category
) {
}
