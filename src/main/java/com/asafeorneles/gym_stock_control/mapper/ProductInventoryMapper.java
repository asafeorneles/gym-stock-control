package com.asafeorneles.gym_stock_control.mapper;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryLowStockThresholdDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryQuantityDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventoryDto;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;

public class ProductInventoryMapper {

    public static ResponseProductInventoryDto productInventoryToResponseProductInventory(ProductInventory productInventory){
        return new ResponseProductInventoryDto(
                productInventory.getProductInventoryId(),
                productInventory.getQuantity(),
                productInventory.getLowStockThreshold()
        );
    }

    public static void patchProductInventoryQuantity(ProductInventory productInventory, PatchProductInventoryQuantityDto patchProductInventoryQuantity){
        productInventory.setQuantity(patchProductInventoryQuantity.quantity());
    }

    public static void patchProductInventoryLowStockThreshold(ProductInventory productInventory, PatchProductInventoryLowStockThresholdDto patchProductInventoryLowStockThreshold){
        productInventory.setLowStockThreshold(patchProductInventoryLowStockThreshold.lowStockThreshold());
    }
}
