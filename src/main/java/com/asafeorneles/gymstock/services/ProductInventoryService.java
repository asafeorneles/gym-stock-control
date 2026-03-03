package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.ProductInventory.ProductInventoryDetailResponse;
import com.asafeorneles.gymstock.dtos.ProductInventory.ProductInventoryLowStockThresholdRequest;
import com.asafeorneles.gymstock.dtos.ProductInventory.ProductInventoryQuantityRequest;
import com.asafeorneles.gymstock.entities.Product;
import com.asafeorneles.gymstock.entities.ProductInventory;
import com.asafeorneles.gymstock.entities.SaleItem;
import com.asafeorneles.gymstock.enums.InventoryStatus;
import com.asafeorneles.gymstock.exceptions.InsufficientProductQuantityException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.ProductInventoryMapper;
import com.asafeorneles.gymstock.repositories.ProductInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductInventoryService {

    final ProductInventoryRepository productInventoryRepository;
    final ProductInventoryMapper productInventoryMapper;

    public List<ProductInventoryDetailResponse> findProductsInventories() {
        return productInventoryRepository.findAll()
                .stream()
                .map(productInventoryMapper::toResponseDetail)
                .toList();
    }

    public ProductInventoryDetailResponse findProductInventoryById(UUID id) {
        return productInventoryRepository.findById(id)
                .map(productInventoryMapper::toResponseDetail)
                .orElseThrow(() -> new ResourceNotFoundException("Product Inventory not found by this id: " + id));
    }

    @Transactional
    public ProductInventoryDetailResponse updateQuantity(UUID id, ProductInventoryQuantityRequest patchProductInventoryQuantity) {
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Inventory not found by this id: " + id));

        Product product = productInventoryFound.getProduct();

        ProductService.checkProductIsActiveBeforeUpdate(product.isActivity(), "This product is inactive. You can only update the inventory of products in the activity.");

        productInventoryFound.setQuantity(patchProductInventoryQuantity.quantity());

        assignInventoryStatus(productInventoryFound);

        productInventoryRepository.save(productInventoryFound);

        return productInventoryMapper.toResponseDetail(productInventoryFound);
    }

    @Transactional
    public ProductInventoryDetailResponse updateLowStockThreshold(UUID id, ProductInventoryLowStockThresholdRequest patchProductInventoryLowStockThreshold) {
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Inventory not found by this id: " + id));

        Product product = productInventoryFound.getProduct();

        ProductService.checkProductIsActiveBeforeUpdate(product.isActivity(), "This product is inactive. You can only update the inventory of products in the activity.");

        productInventoryFound.setLowStockThreshold(patchProductInventoryLowStockThreshold.lowStockThreshold());

        assignInventoryStatus(productInventoryFound);

        productInventoryRepository.save(productInventoryFound);

        return productInventoryMapper.toResponseDetail(productInventoryFound);
    }

    @Transactional
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
