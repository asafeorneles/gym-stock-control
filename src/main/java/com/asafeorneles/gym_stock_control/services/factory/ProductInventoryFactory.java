package com.asafeorneles.gym_stock_control.services.factory;

import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;
import com.asafeorneles.gym_stock_control.enums.InventoryStatus;

public class ProductInventoryFactory {
    public static ProductInventory newProductInventory(Product product, int quantity, int lowStockThreshold) {

        InventoryStatus inventoryStatus = quantity > lowStockThreshold ? InventoryStatus.OK : InventoryStatus.LOW_STOCK;

        return ProductInventory.builder()
                .product(product)
                .quantity(quantity)
                .lowStockThreshold(lowStockThreshold)
                .inventoryStatus(inventoryStatus)
                .build();
    }
}
