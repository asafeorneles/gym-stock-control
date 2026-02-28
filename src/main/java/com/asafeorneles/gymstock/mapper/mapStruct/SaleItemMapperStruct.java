package com.asafeorneles.gymstock.mapper.mapStruct;

import com.asafeorneles.gymstock.dtos.SaleItem.ResponseSaleItemDto;
import com.asafeorneles.gymstock.entities.SaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SaleItemMapperStruct {

    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productId", source = "product.productId")
    ResponseSaleItemDto toResponse(SaleItem saleItem);
}
