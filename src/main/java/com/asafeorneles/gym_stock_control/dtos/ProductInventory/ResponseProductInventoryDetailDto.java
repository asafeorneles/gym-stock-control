package com.asafeorneles.gym_stock_control.dtos.ProductInventory;

import java.util.UUID;

public record ResponseProductInventoryDetailDto(
        UUID productId,
        String productName,
        int quantity,
        int lowStockThreshold
) {
}
