package com.asafeorneles.gymstock.dtos.ProductInventory;

import com.asafeorneles.gymstock.enums.InventoryStatus;

import java.util.UUID;

public record ResponseProductInventoryDetailDto(
        UUID productId,
        String productName,
        int quantity,
        int lowStockThreshold,
        InventoryStatus inventoryStatus
) {
}
