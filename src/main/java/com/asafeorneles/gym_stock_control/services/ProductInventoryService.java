package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventory;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;
import com.asafeorneles.gym_stock_control.mapper.ProductInventoryMapper;
import com.asafeorneles.gym_stock_control.repositories.ProductInventoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.List;

@Service
public class ProductInventoryService {


    private final ProductInventoryRepository productInventoryRepository;

    public ProductInventoryService(ProductInventoryRepository productInventoryRepository) {
        this.productInventoryRepository = productInventoryRepository;
    }

    public List<ResponseProductInventory> findProducts() {
        List<ProductInventory> productsInventoriesFound = productInventoryRepository.findAll();
        if (productsInventoriesFound.isEmpty()){
            throw new ErrorResponseException(HttpStatus.NOT_FOUND); // Create an Exception Handler for when ProductInventory is not found
        }
        return productsInventoriesFound.stream()
                .map(ProductInventoryMapper::productInventoryToResponseProductInventory)
                .toList();
    }
}
