package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.category.CategoryDetailsResponse;
import com.asafeorneles.gymstock.dtos.category.CategoryUpdateRequest;
import com.asafeorneles.gymstock.dtos.category.CategoryCreateRequest;
import com.asafeorneles.gymstock.dtos.category.CategoryResponse;
import com.asafeorneles.gymstock.entities.Category;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CategoryMapper {
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "activityStatus", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    Category toEntity(CategoryCreateRequest categoryCreateRequest);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "activityStatus", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    void updateEntity(CategoryUpdateRequest categoryUpdateRequest, @MappingTarget Category category);

    CategoryDetailsResponse toResponseDetails(Category category);

    CategoryResponse toResponse(Category category);
}
