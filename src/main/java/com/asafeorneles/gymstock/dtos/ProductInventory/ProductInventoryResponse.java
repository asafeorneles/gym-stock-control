package com.asafeorneles.gymstock.dtos.ProductInventory;

import com.asafeorneles.gymstock.enums.InventoryStatus;

public record ProductInventoryResponse(
        int quantity,
        int lowStockThreshold,
        InventoryStatus inventoryStatus
) {
}
