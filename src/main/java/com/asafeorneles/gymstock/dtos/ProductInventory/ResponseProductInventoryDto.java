package com.asafeorneles.gymstock.dtos.ProductInventory;

import com.asafeorneles.gymstock.enums.InventoryStatus;

public record ResponseProductInventoryDto(
        int quantity,
        int lowStockThreshold,
        InventoryStatus inventoryStatus
) {
}
