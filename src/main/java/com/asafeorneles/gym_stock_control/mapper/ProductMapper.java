package com.asafeorneles.gym_stock_control.mapper;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventory;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.product.CreateProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.UpdateProductDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;

public class ProductMapper {
    public static ResponseProductDto productToResponseProduct(Product product) {
        return new ResponseProductDto(
                product.getProductId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getCostPrice(),
                new ResponseCategoryDto(product.getCategory().getCategoryId(), product.getCategory().getName(), product.getCategory().getDescription()),
                new ResponseProductInventory(product.getInventory().getProductInventoryId(), product.getInventory().getQuantity(), product.getInventory().getLowStockThreshold())
        );
    }

    public static Product createProductToProduct(CreateProductDto createProductDto, Category category){
        return Product.builder()
                .name(createProductDto.name())
                .brand(createProductDto.brand())
                .price(createProductDto.price())
                .costPrice(createProductDto.costPrice())
                .category(category)
                .build();
    }

    public static void updateProductToProduct(UpdateProductDto updateProductDto, Product product, Category category){
        product.setName(updateProductDto.name());
        product.setBrand(updateProductDto.brand());
        product.setPrice(updateProductDto.price());
        product.setCostPrice(updateProductDto.costPrice());
        product.setCategory(category);

        if (product.getInventory() == null){
            throw new RuntimeException(); // Create an Exception Handler for when Inventory is null
        } else {
            product.getInventory().setQuantity(updateProductDto.quantity());
            product.getInventory().setLowStockThreshold(updateProductDto.lowStockThreshold());
        }
    }
}
