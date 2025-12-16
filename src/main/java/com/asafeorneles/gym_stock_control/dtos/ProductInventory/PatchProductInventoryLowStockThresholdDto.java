package com.asafeorneles.gym_stock_control.dtos.ProductInventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PatchProductInventoryLowStockThresholdDto(
        @NotNull(message = "The low stock threshold cannot be null!")
        @Min(value = 1, message = "The low stock threshold cannot be less than 1!")
        int lowStockThreshold
) {
}
