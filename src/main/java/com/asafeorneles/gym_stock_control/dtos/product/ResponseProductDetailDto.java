package com.asafeorneles.gym_stock_control.dtos.product;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDetailsDto;
import com.asafeorneles.gym_stock_control.enums.ActivityStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseProductDetailDto(
        UUID productId,
        String name,
        String brand,
        String description,
        BigDecimal price,
        BigDecimal costPrice,
        ResponseCategoryDetailsDto category,
        ResponseProductInventoryDto inventory,
        ActivityStatus activityStatus,
        String InactivityReason
) {
}
