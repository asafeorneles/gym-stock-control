package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryLowStockThreshold;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryQuantity;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventory;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;
import com.asafeorneles.gym_stock_control.entities.SaleItem;
import com.asafeorneles.gym_stock_control.mapper.ProductInventoryMapper;
import com.asafeorneles.gym_stock_control.repositories.ProductInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.UUID;

@Service
public class ProductInventoryService {

    @Autowired
    ProductInventoryRepository productInventoryRepository;

    public List<ResponseProductInventory> findProductsInventories() {
        List<ProductInventory> productsInventoriesFound = productInventoryRepository.findAll();
        if (productsInventoriesFound.isEmpty()){
            throw new ErrorResponseException(HttpStatus.NOT_FOUND); // Create an Exception Handler for when ProductInventory is not found
        }
        return productsInventoriesFound.stream()
                .map(ProductInventoryMapper::productInventoryToResponseProductInventory)
                .toList();
    }

    public ResponseProductInventory updateQuantity(UUID id, PatchProductInventoryQuantity patchProductInventoryQuantity){
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));// Create an Exception Handler for when ProductInventory is not found

        ProductInventoryMapper.patchProductInventoryQuantity(productInventoryFound, patchProductInventoryQuantity);
        productInventoryRepository.save(productInventoryFound);

        return ProductInventoryMapper.productInventoryToResponseProductInventory(productInventoryFound);
    }

    public ResponseProductInventory updateLowStockThreshold(UUID id, PatchProductInventoryLowStockThreshold patchProductInventoryLowStockThreshold) {
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));// Create an Exception Handler for when ProductInventory is not found

        ProductInventoryMapper.patchProductInventoryLowStockThreshold(productInventoryFound, patchProductInventoryLowStockThreshold);
        productInventoryRepository.save(productInventoryFound);

        return ProductInventoryMapper.productInventoryToResponseProductInventory(productInventoryFound);
    }

    public void updateQuantity(List<SaleItem> saleItems){
        for (SaleItem saleItem : saleItems){
            int quantitySold = saleItem.getQuantity();
            ProductInventory inventory = saleItem.getProduct().getInventory();

            inventory.setQuantity(inventory.getQuantity() - quantitySold);
            productInventoryRepository.save(inventory);
        }
    }
}
