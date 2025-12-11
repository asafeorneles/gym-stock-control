package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.product.CreateProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.UpdateProductDto;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;
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
import java.util.List;
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
    private Product productLowStock;
    private CreateProductDto createProductDto;
    private UpdateProductDto updateProductDto;
    private Category category;

    @Captor
    ArgumentCaptor<Product> productArgumentCaptor;

    @Captor
    ArgumentCaptor<UUID> productIdArgumentCaptor;

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

        ProductInventory inventory = ProductInventory.builder()
                .productInventoryId(product.getProductId())
                .product(product)
                .quantity(10)
                .lowStockThreshold(5)
                .build();
        product.setInventory(inventory);

        productLowStock = Product.builder()
                .productId(UUID.randomUUID())
                .name("Hipercalórico")
                .brand("Growth")
                .price(BigDecimal.valueOf(100.99))
                .costPrice(BigDecimal.valueOf(69.99))
                .category(category)
                .build();

        ProductInventory inventoryLowStock = ProductInventory.builder()
                .productInventoryId(productLowStock.getProductId())
                .product(productLowStock)
                .quantity(4)
                .lowStockThreshold(5)
                .build();
        productLowStock.setInventory(inventoryLowStock);

        createProductDto = new CreateProductDto(
                "Whey",
                "Growth",
                BigDecimal.valueOf(100.99),
                BigDecimal.valueOf(69.99),
                category.getCategoryId(),
                35,
                8
        );

        updateProductDto = new UpdateProductDto(
                "Whey de Baunilha",
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
        void shouldCreateAProductSuccessfully() {
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
        void shouldThrowAExceptionWhenCategoryDoesNotExist() {
            // ARRANGE
            when(categoryRepository.findById(createProductDto.categoryId())).thenThrow(new ErrorResponseException(HttpStatus.NOT_FOUND));

            // ASSERTS
            assertThrows(ErrorResponseException.class, () -> productService.createProduct(createProductDto));
            verify(categoryRepository, times(1)).findById(createProductDto.categoryId());
        }

        @Test
        void shouldThrowAExceptionWhenProductIsNotCreate() {
            // ARRANGE
            when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException());
            when(categoryRepository.findById(createProductDto.categoryId())).thenReturn(Optional.of(category));
            when(productRepository.existsByNameAndBrand(createProductDto.name(), createProductDto.brand())).thenReturn(false);
            // ASSERTS
            assertThrows(RuntimeException.class, () -> productService.createProduct(createProductDto));
            verify(productRepository, times(1)).save(any(Product.class));
            verify(categoryRepository, times(1)).findById(createProductDto.categoryId());
            verify(productRepository, times(1)).existsByNameAndBrand(createProductDto.name(), createProductDto.brand());

        }

        @Test
        void shouldThrowAExceptionWhenPetAlreadyExists() {
            // ARRANGE
            when(categoryRepository.findById(createProductDto.categoryId())).thenReturn(Optional.of(category));
            when(productRepository.existsByNameAndBrand(createProductDto.name(), createProductDto.brand())).thenReturn(true);

            // ASSERTS
            assertThrows(IllegalArgumentException.class, () -> productService.createProduct(createProductDto));
        }
    }

    @Nested
    class findProducts {
        @Test
        void shouldFindAllProductsSuccessfully() {
            // ARRANGE
            when(productRepository.findAll()).thenReturn(List.of(product));
            // ACT
            List<ResponseProductDto> productsFound = productService.findProducts();
            // ASSERT
            assertFalse(productsFound.isEmpty());
            verify(productRepository, times(1)).findAll();
            assertEquals(1, productsFound.size());
            assertEquals(product.getProductId(), productsFound.get(0).productId());
        }

        @Test
        void shouldThrowExceptionWhenProductIsNotCreate() {
            // ASSERT
            when(productRepository.findAll()).thenReturn(List.of());

            // ASSERT
            assertThrows(RuntimeException.class, () -> productService.findProducts());
            verify(productRepository, times(1)).findAll();
        }

    }

    @Nested
    class findProductById {
        @Test
        void shouldFindAProductByIdSuccessfully() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            // ACT
            ResponseProductDto responseProduct = productService.findProductById(product.getProductId());
            // ASSERT
            verify(productRepository, times(1)).findById(productIdArgumentCaptor.capture());
            UUID productIdCaptured = productIdArgumentCaptor.getValue();
            assertNotNull(responseProduct);
            assertEquals(productIdCaptured, product.getProductId());

        }

        @Test
        void shouldThrowExceptionWhenProductIsNotFound() {
            UUID falseId = UUID.randomUUID();
            // ARRANGE
            when(productRepository.findById(falseId)).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(ErrorResponseException.class, () -> productService.findProductById(falseId));
            verify(productRepository, times(1)).findById(falseId);
        }
    }

    @Nested
    class findProductsWithLowStock {
        @Test
        void shouldFindAProductWithLowStockIdSuccessfully() {
            // ARRANGE
            when(productRepository.findProductWithLowStock()).thenReturn(List.of(productLowStock));
            // ACT
            List<ResponseProductDto> productsWithLowStockFound = productService.findProductsWithLowStock();
            // ASSERT
            verify(productRepository, times(1)).findProductWithLowStock();
            assertFalse(productsWithLowStockFound.isEmpty());
            assertEquals(1, productsWithLowStockFound.size());

            ResponseProductDto responseProductDto = productsWithLowStockFound.get(0);

            assertEquals(productLowStock.getProductId(), responseProductDto.productId());
            assertEquals(productLowStock.getName(), responseProductDto.name());
            assertEquals(productLowStock.getInventory().getQuantity(), responseProductDto.inventory().quantity());
            assertEquals(productLowStock.getInventory().getLowStockThreshold(), responseProductDto.inventory().lowStockThreshold());

        }

        @Test
        void shouldThrowExceptionWhenProductWithLowStockIsNotFound() {
            // ARRANGE
            when(productRepository.findProductWithLowStock()).thenReturn(List.of());

            // ASSERT
            assertThrows(ErrorResponseException.class, () -> productService.findProductsWithLowStock());
            verify(productRepository, times(1)).findProductWithLowStock();
        }
    }

    @Nested
    class updateProduct {
        @Test
        void shouldUpdateAProductSuccessfully() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(categoryRepository.findById(product.getCategory().getCategoryId())).thenReturn(Optional.of(product.getCategory()));
            when(productRepository.save(any(Product.class))).thenReturn(new Product());

            // ACT
            ResponseProductDto responseProductDto = productService.updateProduct(product.getProductId(), updateProductDto);

            // ASSERT
            verify(productRepository).save(productArgumentCaptor.capture());
            Product productCaptured = productArgumentCaptor.getValue();

            assertNotNull(responseProductDto);
            assertEquals(product.getProductId(), productCaptured.getProductId());

            //Product -> ProductUpdated
            assertEquals(updateProductDto.name(), productCaptured.getName());
            assertEquals(updateProductDto.brand(), productCaptured.getBrand());
            assertEquals(updateProductDto.price(), productCaptured.getPrice());
            assertEquals(updateProductDto.costPrice(), productCaptured.getCostPrice());
            assertEquals(updateProductDto.categoryId(), productCaptured.getCategory().getCategoryId());
            assertEquals(updateProductDto.quantity(), productCaptured.getInventory().getQuantity());
            assertEquals(updateProductDto.lowStockThreshold(), productCaptured.getInventory().getLowStockThreshold());

            //UpdateProduct -> RespondeProductDto
            assertEquals(updateProductDto.name(), responseProductDto.name());
            assertEquals(updateProductDto.brand(), responseProductDto.brand());
            assertEquals(updateProductDto.price(), responseProductDto.price());
            assertEquals(updateProductDto.costPrice(), responseProductDto.costPrice());
            assertEquals(updateProductDto.categoryId(), responseProductDto.category().categoryId());
            assertEquals(updateProductDto.quantity(), responseProductDto.inventory().quantity());
            assertEquals(updateProductDto.lowStockThreshold(), responseProductDto.inventory().lowStockThreshold());
        }

        @Test
        void shouldThrowExceptionWhenProductNotFound() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());

            // ASSERTS
            assertThrows(ErrorResponseException.class, () -> productService.updateProduct(product.getProductId(), updateProductDto));
            verify(productRepository, times(1)).findById(product.getProductId());
        }

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExist() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(categoryRepository.findById((product.getCategory().getCategoryId()))).thenReturn(Optional.empty());

            // ASSERTS
            assertThrows(ErrorResponseException.class, () -> productService.updateProduct(product.getProductId(), updateProductDto));
            verify(productRepository, times(1)).findById(product.getProductId());
            verify(categoryRepository, times(1)).findById(product.getCategory().getCategoryId());
        }

        @Test
        void shouldThrowExceptionWhenProductIsNotUpdate() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(categoryRepository.findById((product.getCategory().getCategoryId()))).thenReturn(Optional.of(product.getCategory()));
            when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException());

            // ASSERTS
            assertThrows(RuntimeException.class, () -> productService.updateProduct(product.getProductId(), updateProductDto));
            verify(productRepository, times(1)).findById(product.getProductId());
            verify(categoryRepository, times(1)).findById(product.getCategory().getCategoryId());
        }

    }
    @Nested
    class deleteProduct {
        @Test
        void shouldDeleteAProductsSuccessfully(){
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            doNothing().when(productRepository).delete(product);

            // ACT
            productService.deleteProduct(product.getProductId());

            // ASSERT
            verify(productRepository, times(1)).findById(productIdArgumentCaptor.capture());
            verify(productRepository, times(1)).delete(productArgumentCaptor.capture());

            UUID idCaptured = productIdArgumentCaptor.getValue();
            Product productCaptured = productArgumentCaptor.getValue();

            assertEquals(product.getProductId(), idCaptured);
            assertEquals(product, productCaptured);
        }

        @Test
        void shouldThrowExceptionWhenProductNotFound() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());

            // ASSERTS
            assertThrows(ErrorResponseException.class, () -> productService.deleteProduct(product.getProductId()));
            verify(productRepository, times(1)).findById(product.getProductId());
        }
    }
}