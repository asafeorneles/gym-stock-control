package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.product.ProductCreateRequest;
import com.asafeorneles.gymstock.dtos.product.ProductDetailResponse;
import com.asafeorneles.gymstock.dtos.product.ProductResponse;
import com.asafeorneles.gymstock.dtos.product.ProductUpdateRequest;
import com.asafeorneles.gymstock.entities.Product;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring",
        uses = {CategoryMapper.class, ProductInventoryMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface ProductMapper {
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "activityStatus", ignore = true)
    @Mapping(target = "inactivityReason", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductCreateRequest productCreateRequest);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "activityStatus", ignore = true)
    @Mapping(target = "inactivityReason", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntity(ProductUpdateRequest productUpdateRequest, @MappingTarget Product product);

    ProductDetailResponse toResponseDetails(Product product);

    ProductResponse toResponse(Product product);
}
