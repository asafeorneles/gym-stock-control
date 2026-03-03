package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.category.CategoryCreateRequest;
import com.asafeorneles.gymstock.dtos.category.CategoryUpdateRequest;
import com.asafeorneles.gymstock.dtos.category.CategoryDetailsResponse;
import com.asafeorneles.gymstock.entities.Category;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.CategoryMapper;
import com.asafeorneles.gymstock.repositories.CategoryRepository;
import com.asafeorneles.gymstock.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    final CategoryRepository categoryRepository;
    final  ProductRepository productRepository;
    final CategoryMapper categoryMapper;

    @Transactional
    public CategoryDetailsResponse createCategory(CategoryCreateRequest categoryCreateRequest) {
        Category category = categoryMapper.toEntity(categoryCreateRequest);
        category.activity();
        categoryRepository.save(category);
        return categoryMapper.toResponseDetails(category);
    }

    public List<CategoryDetailsResponse> getAllCategories(Specification<Category> specification) {
        return categoryRepository.findAll(specification)
                .stream()
                .map(categoryMapper::toResponseDetails)
                .toList();
    }

    public CategoryDetailsResponse getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponseDetails)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));
    }

    @Transactional
    public CategoryDetailsResponse updateCategory(UUID id, CategoryUpdateRequest categoryUpdateRequest) {
        Category category = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));

        checkCategoryIsActiveBeforeUpdate(category.isActivity(), "This category is inactive. You can only update active categories.");

        categoryMapper.updateEntity(categoryUpdateRequest, category);

        categoryRepository.save(category);

        return categoryMapper.toResponseDetails(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        if (productRepository.existsByCategory_CategoryId(id)) {
            throw new BusinessConflictException("This category has already been used in a product. Please use the deactivate option.");
        }

        Category category = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));

        categoryRepository.delete(category);
    }

    @Transactional
    public CategoryDetailsResponse activateCategory(UUID id) {
        Category category = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));

        category.activity();

        categoryRepository.save(category);

        return categoryMapper.toResponseDetails(category);
    }

    @Transactional
    public CategoryDetailsResponse deactivateCategory(UUID id) {
        Category category = categoryRepository
                .findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found by id: " + id));

        category.inactivity();

        categoryRepository.save(category);

        return categoryMapper.toResponseDetails(category);
    }

    public static void checkCategoryIsActiveBeforeUpdate(boolean isActive, String error) {
        if (!isActive) {
            throw new BusinessConflictException(error);
        }
    }
}
