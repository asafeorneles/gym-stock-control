package com.asafeorneles.gym_stock_control.mapper;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDetailsDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.product.CreateProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDetailDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.UpdateProductDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;

public class ProductMapper {
    public static Product createProductToProduct(CreateProductDto createProductDto, Category category) {
        return Product.builder()
                .name(createProductDto.name())
                .brand(createProductDto.brand())
                .description(createProductDto.description())
                .price(createProductDto.price())
                .costPrice(createProductDto.costPrice())
                .category(category)
                .build();
    }

    public static void updateProductToProduct(UpdateProductDto updateProductDto, Product product, Category category) {
        product.setName(updateProductDto.name());
        product.setBrand(updateProductDto.brand());
        product.setDescription(updateProductDto.description());
        product.setPrice(updateProductDto.price());
        product.setCostPrice(updateProductDto.costPrice());
        product.setCategory(category);
    }

    public static ResponseProductDetailDto productToResponseDetailsProduct(Product product) {
        return new ResponseProductDetailDto(
                product.getProductId(),
                product.getName(),
                product.getBrand(),
                product.getDescription(),
                product.getPrice(),
                product.getCostPrice(),
                new ResponseCategoryDetailsDto(product.getCategory().getCategoryId(), product.getCategory().getName(), product.getCategory().getDescription(), product.getCategory().getActivityStatus()),
                new ResponseProductInventoryDto(product.getInventory().getQuantity(), product.getInventory().getLowStockThreshold(), product.getInventory().getInventoryStatus()),
                product.getActivityStatus(),
                product.getInactivityReason()
        );
    }

    public static ResponseProductDto productToResponseProduct(Product product) {
        return new ResponseProductDto(
                product.getProductId(),
                product.getName(),
                product.getBrand(),
                product.getDescription(),
                product.getPrice(),
                new ResponseCategoryDto(product.getCategory().getCategoryId(), product.getCategory().getName())
        );
    }
}
