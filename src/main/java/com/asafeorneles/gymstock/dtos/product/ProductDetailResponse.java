package com.asafeorneles.gymstock.dtos.product;

import com.asafeorneles.gymstock.dtos.ProductInventory.ProductInventoryResponse;
import com.asafeorneles.gymstock.dtos.category.CategoryDetailsResponse;
import com.asafeorneles.gymstock.enums.ActivityStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductDetailResponse(
        UUID productId,
        String name,
        String brand,
        String description,
        BigDecimal price,
        BigDecimal costPrice,
        CategoryDetailsResponse category,
        ProductInventoryResponse inventory,
        ActivityStatus activityStatus,
        String inactivityReason
) {
}
