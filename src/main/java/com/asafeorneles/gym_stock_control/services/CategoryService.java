package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.category.CreateCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDetailsDto;
import com.asafeorneles.gym_stock_control.dtos.category.UpdateCategoryDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.exceptions.BusinessConflictException;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.mapper.CategoryMapper;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public ResponseCategoryDetailsDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = CategoryMapper.createCategoryToCategory(createCategoryDto);
        category.activity();
        categoryRepository.save(category);
        return CategoryMapper.categoryToResponseCategoryDetails(category);
    }

    public List<ResponseCategoryDetailsDto> getAllCategories(Specification<Category> specification) {
        return categoryRepository.findAll(specification).stream().map(CategoryMapper::categoryToResponseCategoryDetails).toList();
    }

    public ResponseCategoryDetailsDto getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(CategoryMapper::categoryToResponseCategoryDetails)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));
    }

    @Transactional
    public ResponseCategoryDetailsDto updateCategory(UUID id, UpdateCategoryDto updateCategoryDto) {
        Category categoryFound = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));

        checkCategoryIsActiveBeforeUpdate(categoryFound.isActivity(), "This category is inactive. You can only update active categories.");

        CategoryMapper.updateCategoryToCategory(categoryFound, updateCategoryDto);

        categoryRepository.save(categoryFound);

        return CategoryMapper.categoryToResponseCategoryDetails(categoryFound);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        if (productRepository.existsByCategory_CategoryId(id)) {
            throw new BusinessConflictException("This category has already been used in a product. Please use the deactivate option.");
        }

        Category categoryFound = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));

        categoryRepository.delete(categoryFound);
    }

    @Transactional
    public ResponseCategoryDetailsDto activateCategory(UUID id) {
        Category categoryFound = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));

        categoryFound.activity();

        categoryRepository.save(categoryFound);

        return CategoryMapper.categoryToResponseCategoryDetails(categoryFound);
    }

    @Transactional
    public ResponseCategoryDetailsDto deactivateCategory(UUID id) {
        Category categoryFound = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));

        categoryFound.inactivity();

        categoryRepository.save(categoryFound);

        return CategoryMapper.categoryToResponseCategoryDetails(categoryFound);
    }

    public static void checkCategoryIsActiveBeforeUpdate(boolean isActive, String error) {
        if (!isActive) {
            throw new BusinessConflictException(error);
        }
    }
}
