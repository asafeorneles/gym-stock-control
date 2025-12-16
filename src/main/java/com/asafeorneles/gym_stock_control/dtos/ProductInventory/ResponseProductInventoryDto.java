package com.asafeorneles.gym_stock_control.dtos.ProductInventory;

import java.util.UUID;

public record ResponseProductInventoryDto(
        UUID productId,
        int quantity,
        int lowStockThreshold
) {
}
