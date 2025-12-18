package com.asafeorneles.gym_stock_control.dtos.product;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseProductDetailDto(
        UUID productId,
        String name,
        String brand,
        BigDecimal price,
        BigDecimal costPrice,
        ResponseCategoryDto category,
        ResponseProductInventoryDto inventory
) {
}
