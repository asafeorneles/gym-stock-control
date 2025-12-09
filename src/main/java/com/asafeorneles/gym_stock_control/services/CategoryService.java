package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.category.CreateCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.UpdateCategoryDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.mapper.CategoryMapper;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;

    public ResponseCategoryDto createCategory(CreateCategoryDto createCategoryDto){
        var category = new Category();
        BeanUtils.copyProperties(createCategoryDto, category);
        categoryRepository.save(category);
        return new ResponseCategoryDto(
                category.getCategoryId(),
                category.getName(),
                category.getDescription());
    }

    public List<ResponseCategoryDto> findCategory() {
        List<Category> categoriesFound = categoryRepository.findAll();
        if (categoriesFound.isEmpty()){
            throw new ErrorResponseException(HttpStatus.NOT_FOUND); // Create an Exception Handler for when Category does not exist
        }
        return categoriesFound.stream().map(CategoryMapper::categoryToResponseCategory).toList();
    }

    public ResponseCategoryDto findCategoryById(UUID id) {
        Category categoryFound = categoryRepository
                .findById(id).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));// Create an Exception Handler for when Category does not exist
        return CategoryMapper.categoryToResponseCategory(categoryFound);
    }

    public ResponseCategoryDto updateCategory(UUID id, UpdateCategoryDto updateCategoryDto) {
        Category categoryFound = categoryRepository
                .findById(id).orElseThrow(() -> new ErrorResponseException(HttpStatus.NOT_FOUND));// Create an Exception Handler for when Category does not exist

        CategoryMapper.updateCategoryToCategory(categoryFound, updateCategoryDto);

        categoryRepository.save(categoryFound);

        return CategoryMapper.categoryToResponseCategory(categoryFound);
    }
}
