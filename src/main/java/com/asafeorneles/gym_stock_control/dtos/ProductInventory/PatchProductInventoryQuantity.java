package com.asafeorneles.gym_stock_control.dtos.ProductInventory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PatchProductInventoryQuantity(
        @NotNull(message = "The quantity cannot be null!")
        @Min(value = 1, message = "The min quantity is 1!")
        int quantity
) {
}
