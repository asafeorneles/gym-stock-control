package com.asafeorneles.gym_stock_control.mapper;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryLowStockThresholdDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryQuantityDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventoryDetailDto;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;

public class ProductInventoryMapper {

    public static ResponseProductInventoryDetailDto productInventoryToResponseProductInventoryDetail(ProductInventory productInventory){
        return new ResponseProductInventoryDetailDto(
                productInventory.getProductInventoryId(),
                productInventory.getProduct().getName(),
                productInventory.getQuantity(),
                productInventory.getLowStockThreshold(),
                productInventory.getInventoryStatus()
        );
    }

    public static void patchProductInventoryQuantity(ProductInventory productInventory, PatchProductInventoryQuantityDto patchProductInventoryQuantity){
        productInventory.setQuantity(patchProductInventoryQuantity.quantity());
    }

    public static void patchProductInventoryLowStockThreshold(ProductInventory productInventory, PatchProductInventoryLowStockThresholdDto patchProductInventoryLowStockThreshold){
        productInventory.setLowStockThreshold(patchProductInventoryLowStockThreshold.lowStockThreshold());
    }
}
