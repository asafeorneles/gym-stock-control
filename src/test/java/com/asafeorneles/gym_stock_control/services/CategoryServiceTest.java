package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.category.CreateCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.UpdateCategoryDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.exceptions.CategoryNotFoundException;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.ErrorResponseException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private UpdateCategoryDto updateCategoryDto;

    @Captor
    ArgumentCaptor<Category> categoryArgumentCaptor;

    @Captor
    ArgumentCaptor<UUID> categoryIdArgumentCaptor;

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

        updateCategoryDto = new UpdateCategoryDto(
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

            // ASSERT
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

    @Nested
    class findCategory {
        @Test
        void shouldFindCategoriesSuccessfully(){
            // ARRANGE
            when(categoryRepository.findAll()).thenReturn(List.of(category));

            // ACT
            List<ResponseCategoryDto> responseCategoriesFound = categoryService.findCategory();

            // ASSERT
            assertFalse(responseCategoriesFound.isEmpty());
            assertEquals(1, responseCategoriesFound.size());
            assertEquals(category.getName(), responseCategoriesFound.get(0).name());
            assertEquals(category.getDescription(), responseCategoriesFound.get(0).description());
            verify(categoryRepository, times(1)).findAll();
        }

        @Test
        void shouldThrowExceptionWhenCategoriesIsNotFound(){
            // ARRANGE
            when(categoryRepository.findAll()).thenReturn(List.of());

            // ASSERT
            assertThrows(CategoryNotFoundException.class, ()-> categoryService.findCategory());
            verify(categoryRepository, times(1)).findAll();

        }
    }

    @Nested
    class findCategoryById{
        @Test
        void shouldFindCategoryByIdSuccessfully(){
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));

            // ACT
            ResponseCategoryDto responseCategory = categoryService.findCategoryById(category.getCategoryId());

            // ASSERT
            verify(categoryRepository).findById(categoryIdArgumentCaptor.capture());
            UUID idCaptured = categoryIdArgumentCaptor.getValue();
            assertNotNull(responseCategory);
            assertEquals(category.getCategoryId(), idCaptured);
            assertEquals(category.getName(), responseCategory.name());
            assertEquals(category.getDescription(), responseCategory.description());
        }

        @Test
        void shouldThrowExceptionWhenCategoryIsNotFoundById(){
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(CategoryNotFoundException.class, ()-> categoryService.findCategoryById(category.getCategoryId()));
            verify(categoryRepository, times(1)).findById(category.getCategoryId());

        }
    }

    @Nested
    class updateCategory{
        @Test
        void shouldUpdateACategorySuccessfully(){
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            ResponseCategoryDto responseCategoryDto = categoryService.updateCategory(category.getCategoryId(), updateCategoryDto);

            // ASSERT
            assertNotNull(responseCategoryDto);
            assertEquals(category.getCategoryId(), responseCategoryDto.categoryId());

            verify(categoryRepository).save(categoryArgumentCaptor.capture());
            Category categoryCaptured = categoryArgumentCaptor.getValue();

            // UpdateCategoryDto -> Category
            assertEquals(updateCategoryDto.name(), categoryCaptured.getName());
            assertEquals(updateCategoryDto.description(), categoryCaptured.getDescription());

            // UpdateCategoryDto -> ResponseCategoryDto
            assertEquals(updateCategoryDto.name(), responseCategoryDto.name());
            assertEquals(updateCategoryDto.description(), responseCategoryDto.description());
        }

        @Test
        void shouldThrowExceptionWhenCategoryIsNotFound(){
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(CategoryNotFoundException.class, ()-> categoryService.updateCategory(category.getCategoryId(), updateCategoryDto));
            verify(categoryRepository, times(1)).findById(category.getCategoryId());
        }

        @Test
        void shouldThrowExceptionWhenCategoryIsNotUpdate(){
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenThrow(RuntimeException.class);

            // ASSERT
            assertThrows(RuntimeException.class, ()-> categoryService.updateCategory(category.getCategoryId(), updateCategoryDto));
            verify(categoryRepository, times(1)).findById(category.getCategoryId());
            verify(categoryRepository, times(1)).save(any(Category.class));
        }
    }

    @Nested
    class deleteCategory {
        @Test
        void shouldDeleteACategorySuccessfully(){
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
            doNothing().when(categoryRepository).delete(category);

            // ACT
            categoryService.deleteCategory(category.getCategoryId());

            // ASSERT
            verify(categoryRepository).delete(categoryArgumentCaptor.capture());
            Category categoryCaptured = categoryArgumentCaptor.getValue();

            assertEquals(category.getCategoryId(), categoryCaptured.getCategoryId());
            assertEquals(category, categoryCaptured);
        }

        @Test
        void shouldThrowExceptionWhenCategoryIsNotFound(){
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(CategoryNotFoundException.class, ()-> categoryService.deleteCategory(category.getCategoryId()));
            verify(categoryRepository, times(1)).findById(category.getCategoryId());
        }
    }
}