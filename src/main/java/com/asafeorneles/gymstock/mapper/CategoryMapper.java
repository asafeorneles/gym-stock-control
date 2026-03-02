package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.category.CreateCategoryDto;
import com.asafeorneles.gymstock.dtos.category.ResponseCategoryDetailsDto;
import com.asafeorneles.gymstock.dtos.category.ResponseCategoryDto;
import com.asafeorneles.gymstock.dtos.category.UpdateCategoryDto;
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
    Category toEntity(CreateCategoryDto createCategoryDto);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "activityStatus", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    void updateEntity(UpdateCategoryDto updateCategoryDto, @MappingTarget Category category);

    ResponseCategoryDetailsDto toResponseDetails(Category category);

    ResponseCategoryDto toResponse(Category category);
}
