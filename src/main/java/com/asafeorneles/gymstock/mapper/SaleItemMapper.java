package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.SaleItem.SaleItemResponse;
import com.asafeorneles.gymstock.entities.SaleItem;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface SaleItemMapper {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.productId")
    SaleItemResponse toResponse(SaleItem saleItem);
}
