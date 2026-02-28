package com.asafeorneles.gymstock.mapper.mapStruct;

import com.asafeorneles.gymstock.dtos.ProductInventory.ResponseProductInventoryDetailDto;
import com.asafeorneles.gymstock.dtos.ProductInventory.ResponseProductInventoryDto;
import com.asafeorneles.gymstock.entities.ProductInventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductInventoryMapperStruct {
    @Mapping(target = "productId", source = "product.productId")
    @Mapping(target = "productName", source = "product.name")
    ResponseProductInventoryDetailDto toResponseDetail(ProductInventory productInventory);

    ResponseProductInventoryDto toResponse(ProductInventory productInventory);
}
