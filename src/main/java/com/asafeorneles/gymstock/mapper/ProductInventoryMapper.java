package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.ProductInventory.ProductInventoryDetailResponse;
import com.asafeorneles.gymstock.dtos.ProductInventory.ProductInventoryResponse;
import com.asafeorneles.gymstock.entities.ProductInventory;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductInventoryMapper {
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.name")
    ProductInventoryDetailResponse toResponseDetail(ProductInventory productInventory);

    ProductInventoryResponse toResponse(ProductInventory productInventory);
}
