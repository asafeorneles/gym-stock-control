package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.product.CreateProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @InjectMocks
    ProductService productService;

    private Product product;
    private CreateProductDto createProductDto;
    private Category category;

    @Captor
    ArgumentCaptor <Product> productArgumentCaptor;

    @Captor
    ArgumentCaptor <Category> categoryArgumentCaptor;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .categoryId(UUID.randomUUID())
                .name("Suplementos")
                .description("Alimento em pó para maior eficiência")
                .build();
        product = Product.builder()
                .productId(UUID.randomUUID())
                .name("Hipercalórico")
                .brand("Growth")
                .price(BigDecimal.valueOf(100.99))
                .costPrice(BigDecimal.valueOf(69.99))
                .category(category)
                .build();

        createProductDto = new CreateProductDto(
                "Whey",
                "Growth",
                BigDecimal.valueOf(100.99),
                BigDecimal.valueOf(69.99),
                category.getCategoryId(),
                35,
                8
        );

    }

    @Nested
    class createProduct {
        @Test
        void shouldCreateAProductSuccessfully(){
            // ARRANGE
            when(categoryRepository.findById(createProductDto.categoryId())).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            // ACT
            ResponseProductDto responseProductDto = productService.createProduct(createProductDto);
            // ASSERTS
            verify(productRepository).save(productArgumentCaptor.capture());
            Product productCaptured = productArgumentCaptor.getValue();
            assertNotNull(responseProductDto);

            assertEquals(category, productCaptured.getCategory());

            // CreateProductDto -> Product
            assertEquals(createProductDto.name(), productCaptured.getName());
            assertEquals(createProductDto.brand(), productCaptured.getBrand());
            assertEquals(createProductDto.price(), productCaptured.getPrice());
            assertEquals(createProductDto.costPrice(), productCaptured.getCostPrice());
            assertEquals(createProductDto.categoryId(), productCaptured.getCategory().getCategoryId());
            assertEquals(createProductDto.quantity(), productCaptured.getInventory().getQuantity());
            assertEquals(createProductDto.lowStockThreshold(), productCaptured.getInventory().getLowStockThreshold());

            // CreateProductDto -> ResponseProductDto
            assertEquals(createProductDto.name(), responseProductDto.name());
            assertEquals(createProductDto.brand(), responseProductDto.brand());
            assertEquals(createProductDto.price(), responseProductDto.price());
            assertEquals(createProductDto.costPrice(), responseProductDto.costPrice());
            assertEquals(createProductDto.categoryId(), responseProductDto.category().categoryId());
            assertEquals(createProductDto.quantity(), responseProductDto.inventory().quantity());
            assertEquals(createProductDto.lowStockThreshold(), responseProductDto.inventory().lowStockThreshold());
        }
        @Test
        void shouldThrowAExceptionWhenCategoryDoesNotExist(){
            // ARRANGE
            when(categoryRepository.findById(createProductDto.categoryId())).thenThrow(new ErrorResponseException(HttpStatus.NOT_FOUND));

            // ASSERTS
            assertThrows(ErrorResponseException.class, ()-> productService.createProduct(createProductDto));
        }
    }

    @Test
    void findProducts() {
    }

    @Test
    void findProductById() {
    }

    @Test
    void findProductsWithLowStock() {
    }

    @Test
    void updateProduct() {
    }

    @Test
    void deleteProduct() {
    }
}