package com.asafeorneles.gym_stock_control.mapper;

import com.asafeorneles.gym_stock_control.dtos.category.CreateCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.UpdateCategoryDto;
import com.asafeorneles.gym_stock_control.entities.Category;

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

    public static ResponseCategoryDto categoryToResponseCategory(Category category){
        return new ResponseCategoryDto(
                category.getCategoryId(),
                category.getName(),
                category.getDescription()
        );
    }

}
