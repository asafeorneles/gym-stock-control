package com.asafeorneles.gymstock.dtos.product;

import com.asafeorneles.gymstock.dtos.ProductInventory.ResponseProductInventoryDto;
import com.asafeorneles.gymstock.dtos.category.ResponseCategoryDetailsDto;
import com.asafeorneles.gymstock.enums.ActivityStatus;

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
