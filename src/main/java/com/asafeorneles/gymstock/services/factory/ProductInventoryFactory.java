package com.asafeorneles.gymstock.services.factory;

import com.asafeorneles.gymstock.entities.Product;
import com.asafeorneles.gymstock.entities.ProductInventory;
import com.asafeorneles.gymstock.enums.InventoryStatus;

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
