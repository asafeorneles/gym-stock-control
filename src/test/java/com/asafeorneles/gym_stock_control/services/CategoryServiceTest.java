package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.category.CreateCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    CategoryService categoryService;

    private Category category;
    private CreateCategoryDto createCategoryDto;
    private ResponseCategoryDto responseCategoryDto;

    @Captor
    ArgumentCaptor<Category> categoryArgumentCaptor;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .categoryId(UUID.randomUUID())
                .name("Suplementos")
                .description("Alimento em pó para maior eficiência")
                .build();

        createCategoryDto = new CreateCategoryDto(
                "Suplementos",
                "Alimento em pó para maior eficiência"
        );

        responseCategoryDto = new ResponseCategoryDto(
                UUID.randomUUID(),
                "Suplementos",
                "Alimento em pó para maior eficiência"
        );
    }

    @Nested
    class createCategory {
        @Test
        void shouldCreateACategorySuccessfully(){
            // ARRANGE
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // ACT
            ResponseCategoryDto responseCategory = categoryService.createCategory(createCategoryDto);

            // ASSERTS
            verify(categoryRepository).save(categoryArgumentCaptor.capture());
            Category categoryCaptured = categoryArgumentCaptor.getValue();

            assertNotNull(responseCategory);

            // CreateCategoryDto -> ResponseCategoryDto
            assertEquals(createCategoryDto.name(), responseCategory.name());
            assertEquals(createCategoryDto.description(), responseCategory.description());

            // CreateCategoryDto -> Category
            assertEquals(createCategoryDto.name(), categoryCaptured.getName());
            assertEquals(createCategoryDto.description(), categoryCaptured.getDescription());
        }

        @Test
        void shouldExceptionWhenCategoryIsNotCreate(){
            // ARRANGE
            when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException());

            //ASSERT
            assertThrows(RuntimeException.class, ()-> categoryService.createCategory(createCategoryDto));
            verify(categoryRepository, times(1)).save(any(Category.class));
        }
    }

    @Test
    void findCategory() {
    }

    @Test
    void findCategoryById() {
    }

    @Test
    void updateCategory() {
    }

    @Test
    void deleteCategory() {
    }
}