package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.category.CreateCategoryDto;
import com.asafeorneles.gymstock.dtos.category.ResponseCategoryDetailsDto;
import com.asafeorneles.gymstock.dtos.category.UpdateCategoryDto;
import com.asafeorneles.gymstock.entities.Category;

public class CategoryMapper {
    public static Category createCategoryToCategory(CreateCategoryDto createCategoryDto){
        return Category.builder()
                .name(createCategoryDto.name())
                .description(createCategoryDto.description())
                .build();
    }

    public static void updateCategoryToCategory(Category category, UpdateCategoryDto updateCategoryDto){
        category.setName(updateCategoryDto.name());
        category.setDescription(updateCategoryDto.description());
    }

    public static ResponseCategoryDetailsDto categoryToResponseCategoryDetails(Category category){
        return new ResponseCategoryDetailsDto(
                category.getCategoryId(),
                category.getName(),
                category.getDescription(),
                category.getActivityStatus()
        );
    }

}
