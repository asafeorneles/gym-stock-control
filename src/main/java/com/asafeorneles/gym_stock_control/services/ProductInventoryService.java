package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryLowStockThresholdDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryQuantityDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventoryDetailDto;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;
import com.asafeorneles.gym_stock_control.entities.SaleItem;
import com.asafeorneles.gym_stock_control.enums.InventoryStatus;
import com.asafeorneles.gym_stock_control.exceptions.InsufficientProductQuantityException;
import com.asafeorneles.gym_stock_control.exceptions.ProductInventoryNotFoundException;
import com.asafeorneles.gym_stock_control.mapper.ProductInventoryMapper;
import com.asafeorneles.gym_stock_control.repositories.ProductInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductInventoryService {

    @Autowired
    ProductInventoryRepository productInventoryRepository;

    public List<ResponseProductInventoryDetailDto> findProductsInventories() {
        List<ProductInventory> productsInventoriesFound = productInventoryRepository.findAll();
        if (productsInventoriesFound.isEmpty()) {
            throw new ProductInventoryNotFoundException("Products Inventories not found");
        }
        return productsInventoriesFound.stream()
                .map(ProductInventoryMapper::productInventoryToResponseProductInventoryDetail)
                .toList();
    }

    public ResponseProductInventoryDetailDto findProductInventoryById(UUID id) {
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ProductInventoryNotFoundException("Product Inventory not found by this id: " + id));

        return ProductInventoryMapper.productInventoryToResponseProductInventoryDetail(productInventoryFound);
    }

    public ResponseProductInventoryDetailDto updateQuantity(UUID id, PatchProductInventoryQuantityDto patchProductInventoryQuantity) {
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ProductInventoryNotFoundException("Product Inventory not found by this id: " + id));

        ProductInventoryMapper.patchProductInventoryQuantity(productInventoryFound, patchProductInventoryQuantity);

        assignInventoryStatus(productInventoryFound);

        productInventoryRepository.save(productInventoryFound);

        return ProductInventoryMapper.productInventoryToResponseProductInventoryDetail(productInventoryFound);
    }

    public ResponseProductInventoryDetailDto updateLowStockThreshold(UUID id, PatchProductInventoryLowStockThresholdDto patchProductInventoryLowStockThreshold) {
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ProductInventoryNotFoundException("Product Inventory not found by this id: " + id));

        ProductInventoryMapper.patchProductInventoryLowStockThreshold(productInventoryFound, patchProductInventoryLowStockThreshold);

        assignInventoryStatus(productInventoryFound);

        productInventoryRepository.save(productInventoryFound);

        return ProductInventoryMapper.productInventoryToResponseProductInventoryDetail(productInventoryFound);
    }

    public void updateQuantityAfterSale(List<SaleItem> saleItems) {
        for (SaleItem saleItem : saleItems) {
            int quantitySold = saleItem.getQuantity();
            ProductInventory inventory = saleItem.getProduct().getInventory();

            inventory.setQuantity(inventory.getQuantity() - quantitySold);

            assignInventoryStatus(inventory);

            productInventoryRepository.save(inventory);
        }
    }

    private static void assignInventoryStatus(ProductInventory inventory) {
        InventoryStatus inventoryStatus;
        inventoryStatus =
                inventory.getQuantity() == 0 ? InventoryStatus.OUT_OF_STOCK
                : inventory.getQuantity() <= inventory.getLowStockThreshold() ? InventoryStatus.LOW_STOCK
                : InventoryStatus.OK;
        inventory.setInventoryStatus(inventoryStatus);
    }

    public void validateQuantity(Product product, int quantityToBuy) {
        ProductInventory inventory = product.getInventory();

        int quantityAvailable = inventory.getQuantity();
        if (quantityToBuy > quantityAvailable) {
            throw new InsufficientProductQuantityException("insufficient quantity in stock! Quantity available: " + quantityAvailable);
        }
    }
}
