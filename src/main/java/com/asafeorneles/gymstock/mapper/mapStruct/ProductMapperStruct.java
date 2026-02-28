package com.asafeorneles.gymstock.mapper.mapStruct;

import com.asafeorneles.gymstock.dtos.product.CreateProductDto;
import com.asafeorneles.gymstock.dtos.product.ResponseProductDetailDto;
import com.asafeorneles.gymstock.dtos.product.ResponseProductDto;
import com.asafeorneles.gymstock.dtos.product.UpdateProductDto;
import com.asafeorneles.gymstock.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CategoryMapperStruct.class, ProductInventoryMapperStruct.class})
public interface ProductMapperStruct {
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "activityStatus", ignore = true)
    @Mapping(target = "inactivityReason", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(CreateProductDto createProductDto);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "activityStatus", ignore = true)
    @Mapping(target = "inactivityReason", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntity(UpdateProductDto updateProductDto, @MappingTarget Product product);

    ResponseProductDetailDto toResponseDetails(Product product);

    ResponseProductDto toResponse(Product product);
}
