package com.asafeorneles.gym_stock_control.dtos.ProductInventory;

import com.asafeorneles.gym_stock_control.enums.InventoryStatus;

public record ResponseProductInventoryDto(
        int quantity,
        int lowStockThreshold,
        InventoryStatus inventoryStatus
) {
}
