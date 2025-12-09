package com.asafeorneles.gym_stock_control.dtos.product;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventory;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseProductDto(
        UUID productId,
        String name,
        String brand,
        BigDecimal price,
        BigDecimal costPrice,
        ResponseCategoryDto category,
        ResponseProductInventory inventory
) {
}
