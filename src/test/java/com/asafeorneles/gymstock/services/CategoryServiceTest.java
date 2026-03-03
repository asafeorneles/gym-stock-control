package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.category.CategoryDetailsResponse;
import com.asafeorneles.gymstock.dtos.category.CategoryCreateRequest;
import com.asafeorneles.gymstock.dtos.category.CategoryUpdateRequest;
import com.asafeorneles.gymstock.entities.Category;
import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.exceptions.ActivityStatusException;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.CategoryMapper;
import com.asafeorneles.gymstock.repositories.CategoryRepository;
import com.asafeorneles.gymstock.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Spy
    private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryCreateRequest categoryCreateRequest;
    private CategoryUpdateRequest categoryUpdateRequest;

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
        category.activity();

        categoryCreateRequest = new CategoryCreateRequest(
                "Suplementos",
                "Alimento em pó para maior eficiência"
        );

        categoryUpdateRequest = new CategoryUpdateRequest(
                "Suplementos",
                "Alimento em pó para maior eficiência"
        );

    }

    @Nested
    class createCategory {
        @Test
        void shouldCreateACategorySuccessfully() {
            // ARRANGE
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            // ACT
            CategoryDetailsResponse responseCategory = categoryService.createCategory(categoryCreateRequest);

            // ASSERT
            verify(categoryRepository).save(categoryArgumentCaptor.capture());
            Category categoryCaptured = categoryArgumentCaptor.getValue();

            assertNotNull(responseCategory);

            // CategoryCreateRequest -> CategoryDetailsResponse
            assertEquals(categoryCreateRequest.name(), responseCategory.name());
            assertEquals(categoryCreateRequest.description(), responseCategory.description());

            // CategoryCreateRequest -> Category
            assertEquals(categoryCreateRequest.name(), categoryCaptured.getName());
            assertEquals(categoryCreateRequest.description(), categoryCaptured.getDescription());
        }

        @Test
        void shouldExceptionWhenCategoryIsNotCreate() {
            // ARRANGE
            when(categoryRepository.save(any(Category.class))).thenThrow(new RuntimeException());

            //ASSERT
            assertThrows(RuntimeException.class, () -> categoryService.createCategory(categoryCreateRequest));
            verify(categoryRepository, times(1)).save(any(Category.class));
        }
    }

    @Nested
    class findCategory {
        @Test
        void shouldFindCategoriesSuccessfully() {
            // ARRANGE
            when(categoryRepository.findAll(any(Specification.class))).thenReturn(List.of(category));

            // ACT
            List<CategoryDetailsResponse> responseCategoriesFound = categoryService.getAllCategories(Specification.unrestricted());

            // ASSERT
            assertFalse(responseCategoriesFound.isEmpty());
            assertEquals(1, responseCategoriesFound.size());
            assertEquals(category.getName(), responseCategoriesFound.get(0).name());
            assertEquals(category.getDescription(), responseCategoriesFound.get(0).description());
            verify(categoryRepository, times(1)).findAll(any(Specification.class));
        }

        @Test
        void shouldEmptyListWhenCategoriesIsNotFound() {
            // ARRANGE
            when(categoryRepository.findAll(any(Specification.class))).thenReturn(List.of());

            // ACT
            List<CategoryDetailsResponse> categoryFound = categoryService.getAllCategories(Specification.unrestricted());

            // ASSERT
            assertTrue(categoryFound.isEmpty());
            verify(categoryRepository, times(1)).findAll(any(Specification.class));

        }
    }

    @Nested
    class findCategoryById {
        @Test
        void shouldFindCategoryByIdSuccessfully() {
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));

            // ACT
            CategoryDetailsResponse responseCategory = categoryService.getCategoryById(category.getCategoryId());

            // ASSERT
            verify(categoryRepository).findById(categoryIdArgumentCaptor.capture());
            UUID idCaptured = categoryIdArgumentCaptor.getValue();
            assertNotNull(responseCategory);
            assertEquals(category.getCategoryId(), idCaptured);
            assertEquals(category.getName(), responseCategory.name());
            assertEquals(category.getDescription(), responseCategory.description());
        }

        @Test
        void shouldThrowExceptionWhenCategoryIsNotFoundById() {
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(category.getCategoryId()));
            verify(categoryRepository, times(1)).findById(category.getCategoryId());

        }
    }

    @Nested
    class updateCategory {
        @Test
        void shouldUpdateACategorySuccessfully() {
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenReturn(category);

            CategoryDetailsResponse categoryDetailsResponse = categoryService.updateCategory(category.getCategoryId(), categoryUpdateRequest);

            // ASSERT
            assertNotNull(categoryDetailsResponse);
            assertEquals(category.getCategoryId(), categoryDetailsResponse.categoryId());

            verify(categoryRepository).save(categoryArgumentCaptor.capture());
            Category categoryCaptured = categoryArgumentCaptor.getValue();

            // CategoryUpdateRequest -> Category
            assertEquals(categoryUpdateRequest.name(), categoryCaptured.getName());
            assertEquals(categoryUpdateRequest.description(), categoryCaptured.getDescription());

            // CategoryUpdateRequest -> CategoryDetailsResponse
            assertEquals(categoryUpdateRequest.name(), categoryDetailsResponse.name());
            assertEquals(categoryUpdateRequest.description(), categoryDetailsResponse.description());
        }

        @Test
        void shouldThrowExceptionWhenCategoryIsNotFound() {
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(category.getCategoryId(), categoryUpdateRequest));
            verify(categoryRepository, times(1)).findById(category.getCategoryId());
        }

        @Test
        void shouldThrowExceptionWhenCategoryIsNotUpdate() {
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
            when(categoryRepository.save(any(Category.class))).thenThrow(RuntimeException.class);

            // ASSERT
            assertThrows(RuntimeException.class, () -> categoryService.updateCategory(category.getCategoryId(), categoryUpdateRequest));
            verify(categoryRepository, times(1)).findById(category.getCategoryId());
            verify(categoryRepository, times(1)).save(any(Category.class));
        }
    }

    @Nested
    class deleteCategory {
        @Test
        void shouldDeleteACategorySuccessfully() {
            // ARRANGE
            when(productRepository.existsByCategory_CategoryId(category.getCategoryId())).thenReturn(false);
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
        void shouldThrowExceptionWhenCategoryIsNotFound() {
            // ARRANGE
            when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(category.getCategoryId()));
            verify(categoryRepository, times(1)).findById(category.getCategoryId());
        }

        @Test
        void shouldThrowExceptionWhenCategoryHasBeenUsedInAProduct() {
            // ARRANGE
            when(productRepository.existsByCategory_CategoryId(category.getCategoryId())).thenReturn(true);

            // ASSERT
            assertThrows(BusinessConflictException.class, () -> categoryService.deleteCategory(category.getCategoryId()));
            verify(categoryRepository, never()).findById(category.getCategoryId());
            verify(categoryRepository, never()).delete(category);
        }

        @Nested
        class activateCategory {
            @Test
            void shouldActivateACategoryWithSuccessfully() {
                // ARRANGE
                category.setActivityStatus(ActivityStatus.INACTIVITY);
                when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
                when(categoryRepository.save(any(Category.class))).thenReturn(category);

                // ACT
                CategoryDetailsResponse categoryDetailsResponse = categoryService.activateCategory(category.getCategoryId());

                // ASSERT
                assertNotNull(categoryDetailsResponse);
                assertTrue(category.isActivity());
            }

            @Test
            void shouldThrowExceptionWhenCategoryIsAlreadyActivity() {
                // ARRANGE
                when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));

                // ASSERTS
                assertThrows(ActivityStatusException.class, () -> categoryService.activateCategory(category.getCategoryId()));
                verify(categoryRepository, times(1)).findById(category.getCategoryId());
                verify(categoryRepository, never()).save(any(Category.class));
            }
        }

        @Nested
        class deactivateCategory {
            @Test
            void shouldInactivateACategoryWithSuccessfully() {
                // ARRANGE
                when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
                when(categoryRepository.save(any(Category.class))).thenReturn(category);

                // ACT
                CategoryDetailsResponse categoryDetailsResponse = categoryService.deactivateCategory(category.getCategoryId());

                // ASSERT
                assertNotNull(categoryDetailsResponse);
                assertFalse(category.isActivity());
            }

            @Test
            void shouldThrowExceptionWhenCategoryIsAlreadyInactivity() {
                // ARRANGE
                when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));

                // ASSERTS
                assertThrows(ActivityStatusException.class, () -> categoryService.activateCategory(category.getCategoryId()));
                verify(categoryRepository, times(1)).findById(category.getCategoryId());
                verify(categoryRepository, never()).save(any(Category.class));
            }
        }
    }
}