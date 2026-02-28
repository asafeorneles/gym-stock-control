package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.ProductInventory.PatchProductInventoryLowStockThresholdDto;
import com.asafeorneles.gymstock.dtos.ProductInventory.PatchProductInventoryQuantityDto;
import com.asafeorneles.gymstock.dtos.ProductInventory.ResponseProductInventoryDetailDto;
import com.asafeorneles.gymstock.entities.Product;
import com.asafeorneles.gymstock.entities.ProductInventory;
import com.asafeorneles.gymstock.entities.SaleItem;
import com.asafeorneles.gymstock.enums.InventoryStatus;
import com.asafeorneles.gymstock.exceptions.InsufficientProductQuantityException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.mapStruct.ProductInventoryMapperStruct;
import com.asafeorneles.gymstock.repositories.ProductInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ProductInventoryService {

    @Autowired
    ProductInventoryRepository productInventoryRepository;

    @Autowired
    ProductInventoryMapperStruct productInventoryMapperStruct;

    public List<ResponseProductInventoryDetailDto> findProductsInventories() {
        return productInventoryRepository.findAll()
                .stream()
                .map(productInventory -> productInventoryMapperStruct.toResponseDetail(productInventory))
                .toList();
    }

    public ResponseProductInventoryDetailDto findProductInventoryById(UUID id) {
        return productInventoryRepository.findById(id)
                .map(productInventory -> productInventoryMapperStruct.toResponseDetail(productInventory))
                .orElseThrow(() -> new ResourceNotFoundException("Product Inventory not found by this id: " + id));
    }

    @Transactional
    public ResponseProductInventoryDetailDto updateQuantity(UUID id, PatchProductInventoryQuantityDto patchProductInventoryQuantity) {
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Inventory not found by this id: " + id));

        Product product = productInventoryFound.getProduct();

        ProductService.checkProductIsActiveBeforeUpdate(product.isActivity(), "This product is inactive. You can only update the inventory of products in the activity.");

        productInventoryFound.setQuantity(patchProductInventoryQuantity.quantity());

        assignInventoryStatus(productInventoryFound);

        productInventoryRepository.save(productInventoryFound);

        return productInventoryMapperStruct.toResponseDetail(productInventoryFound);
    }

    @Transactional
    public ResponseProductInventoryDetailDto updateLowStockThreshold(UUID id, PatchProductInventoryLowStockThresholdDto patchProductInventoryLowStockThreshold) {
        ProductInventory productInventoryFound = productInventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product Inventory not found by this id: " + id));

        Product product = productInventoryFound.getProduct();

        ProductService.checkProductIsActiveBeforeUpdate(product.isActivity(), "This product is inactive. You can only update the inventory of products in the activity.");

        productInventoryFound.setLowStockThreshold(patchProductInventoryLowStockThreshold.lowStockThreshold());

        assignInventoryStatus(productInventoryFound);

        productInventoryRepository.save(productInventoryFound);

        return productInventoryMapperStruct.toResponseDetail(productInventoryFound);
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
