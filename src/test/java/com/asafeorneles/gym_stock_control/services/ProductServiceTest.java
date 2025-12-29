package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.product.*;
import com.asafeorneles.gym_stock_control.entities.Category;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.ProductInventory;
import com.asafeorneles.gym_stock_control.enums.ActivityStatus;
import com.asafeorneles.gym_stock_control.exceptions.ActivityStatusException;
import com.asafeorneles.gym_stock_control.exceptions.BusinessConflictException;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.repositories.CategoryRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import com.asafeorneles.gym_stock_control.repositories.SaleItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
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

    @Mock
    SaleItemRepository saleItemRepository;

    @InjectMocks
    ProductService productService;

    private Product product;
    private Product productLowStock;
    private CreateProductDto createProductDto;
    private UpdateProductDto updateProductDto;
    private DeactivateProductDto deactivateProductDto;
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
                .activityStatus(ActivityStatus.ACTIVE)
                .build();
        product = Product.builder()
                .productId(UUID.randomUUID())
                .name("Hipercalórico")
                .brand("Growth")
                .price(BigDecimal.valueOf(100.99))
                .costPrice(BigDecimal.valueOf(69.99))
                .category(category)
                .build();
        product.activity();

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
                "test",
                BigDecimal.valueOf(100.99),
                BigDecimal.valueOf(69.99),
                category.getCategoryId(),
                35,
                8
        );

        updateProductDto = new UpdateProductDto(
                "Whey de Baunilha",
                "Growth",
                "test",
                BigDecimal.valueOf(100.99),
                BigDecimal.valueOf(69.99),
                category.getCategoryId()
        );

        deactivateProductDto = new DeactivateProductDto("test");

    }

    @Nested
    class createProduct {
        @Test
        void shouldCreateAProductSuccessfully() {
            // ARRANGE
            when(categoryRepository.findById(createProductDto.categoryId())).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(productRepository.existsByNameAndBrand(createProductDto.name(), createProductDto.brand())).thenReturn(false);
            // ACT
            ResponseProductDetailDto responseProductDetailDto = productService.createProduct(createProductDto);
            // ASSERTS
            verify(productRepository).save(productArgumentCaptor.capture());
            verify(productRepository, times(1)).existsByNameAndBrand(createProductDto.name(), createProductDto.brand());
            Product productCaptured = productArgumentCaptor.getValue();
            assertNotNull(responseProductDetailDto);

            assertEquals(category, productCaptured.getCategory());

            // CreateProductDto -> Product
            assertEquals(createProductDto.name(), productCaptured.getName());
            assertEquals(createProductDto.brand(), productCaptured.getBrand());
            assertEquals(createProductDto.description(), productCaptured.getDescription());
            assertEquals(createProductDto.price(), productCaptured.getPrice());
            assertEquals(createProductDto.costPrice(), productCaptured.getCostPrice());
            assertEquals(createProductDto.categoryId(), productCaptured.getCategory().getCategoryId());
            assertEquals(createProductDto.quantity(), productCaptured.getInventory().getQuantity());
            assertEquals(createProductDto.lowStockThreshold(), productCaptured.getInventory().getLowStockThreshold());

            // CreateProductDto -> ResponseProductDetailDto
            assertEquals(createProductDto.name(), responseProductDetailDto.name());
            assertEquals(createProductDto.brand(), responseProductDetailDto.brand());
            assertEquals(createProductDto.description(), responseProductDetailDto.description());
            assertEquals(createProductDto.price(), responseProductDetailDto.price());
            assertEquals(createProductDto.costPrice(), responseProductDetailDto.costPrice());
            assertEquals(createProductDto.categoryId(), responseProductDetailDto.category().categoryId());
            assertEquals(createProductDto.quantity(), responseProductDetailDto.inventory().quantity());
            assertEquals(createProductDto.lowStockThreshold(), responseProductDetailDto.inventory().lowStockThreshold());
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
            assertThrows(BusinessConflictException.class, () -> productService.createProduct(createProductDto));
        }

        @Test
        void shouldThrowAExceptionWhenCategoryIsNotActivity() {
            // ARRANGE
            category.inactivity();
            when(categoryRepository.findById(createProductDto.categoryId())).thenReturn(Optional.of(category));

            // ASSERTS
            assertThrows(ActivityStatusException.class, () -> productService.createProduct(createProductDto));
        }
    }

    @Nested
    class findProducts {
        @Test
        void shouldFindAllProductsSuccessfully() {
            // ARRANGE
            when(productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));
            // ACT
            List<ResponseProductDto> productsFound = productService.getAllProducts(Specification.unrestricted());
            // ASSERT
            assertFalse(productsFound.isEmpty());
            verify(productRepository, times(1)).findAll(any(Specification.class));
            assertEquals(1, productsFound.size());
            assertEquals(product.getProductId(), productsFound.get(0).productId());
        }

        @Test
        void shouldReturnEmptyListWhenProductIsNotFound() {
            // ASSERT
            when(productRepository.findAll(any(Specification.class))).thenReturn(List.of());

            // ACT
            List<ResponseProductDto> productsFound = productService.getAllProducts(Specification.unrestricted());

            // ASSERT
            verify(productRepository, times(1)).findAll(any(Specification.class));
            assertTrue(productsFound.isEmpty());
        }

    }

    @Nested
    class findProductById {
        @Test
        void shouldFindAProductByIdSuccessfully() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            // ACT
            ResponseProductDto responseProduct = productService.getProductById(product.getProductId());
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
            assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(falseId));
            verify(productRepository, times(1)).findById(falseId);
        }

        @Test
        void shouldThrowExceptionWhenProductIsNotActivity() {
            product.inactivity("test");
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

            // ASSERT
            assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(product.getProductId()));
            verify(productRepository, times(1)).findById(product.getProductId());
        }
    }

    @Nested
    class findProductsWithLowStock {
        @Test
        void shouldFindAProductWithLowStockIdSuccessfully() {
            // ARRANGE
            when(productRepository.findProductWithLowStock()).thenReturn(List.of(productLowStock));
            // ACT
            List<ResponseProductDetailDto> productsWithLowStockFound = productService.getAllProductsWithLowStock();
            // ASSERT
            verify(productRepository, times(1)).findProductWithLowStock();
            assertFalse(productsWithLowStockFound.isEmpty());
            assertEquals(1, productsWithLowStockFound.size());

            ResponseProductDetailDto responseProductDetailDto = productsWithLowStockFound.get(0);

            assertEquals(productLowStock.getProductId(), responseProductDetailDto.productId());
            assertEquals(productLowStock.getName(), responseProductDetailDto.name());
            assertEquals(productLowStock.getInventory().getQuantity(), responseProductDetailDto.inventory().quantity());
            assertEquals(productLowStock.getInventory().getLowStockThreshold(), responseProductDetailDto.inventory().lowStockThreshold());

        }

        @Test
        void shouldReturnEmptyListWhenProductWithLowStockIsNotFound() {
            // ASSERT
            when(productRepository.findProductWithLowStock()).thenReturn(List.of());

            // ACT
            List<ResponseProductDetailDto> productsFound = productService.getAllProductsWithLowStock();

            // ASSERT
            verify(productRepository, times(1)).findProductWithLowStock();
            assertTrue(productsFound.isEmpty());
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
            ResponseProductDetailDto responseProductDetailDto = productService.updateProduct(product.getProductId(), updateProductDto);

            // ASSERT
            verify(productRepository).save(productArgumentCaptor.capture());
            Product productCaptured = productArgumentCaptor.getValue();

            assertNotNull(responseProductDetailDto);
            assertEquals(product.getProductId(), productCaptured.getProductId());

            //Product -> ProductUpdated
            assertEquals(updateProductDto.name(), productCaptured.getName());
            assertEquals(updateProductDto.brand(), productCaptured.getBrand());
            assertEquals(updateProductDto.price(), productCaptured.getPrice());
            assertEquals(updateProductDto.costPrice(), productCaptured.getCostPrice());
            assertEquals(updateProductDto.categoryId(), productCaptured.getCategory().getCategoryId());

            //UpdateProduct -> RespondeProductDto
            assertEquals(updateProductDto.name(), responseProductDetailDto.name());
            assertEquals(updateProductDto.brand(), responseProductDetailDto.brand());
            assertEquals(updateProductDto.price(), responseProductDetailDto.price());
            assertEquals(updateProductDto.costPrice(), responseProductDetailDto.costPrice());
            assertEquals(updateProductDto.categoryId(), responseProductDetailDto.category().categoryId());
        }

        @Test
        void shouldThrowExceptionWhenProductNotFound() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());

            // ASSERTS
            assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(product.getProductId(), updateProductDto));
            verify(productRepository, times(1)).findById(product.getProductId());
        }

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExist() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(categoryRepository.findById((product.getCategory().getCategoryId()))).thenReturn(Optional.empty());

            // ASSERTS
            assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(product.getProductId(), updateProductDto));
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
        void shouldDeleteAProductsSuccessfully() {
            // ARRANGE
            when(saleItemRepository.existsByProduct_ProductId(product.getProductId())).thenReturn(false);
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
        void shouldThrowExceptionWhenProductHaveAlreadyUsedInASale() {
            // ARRANGE
            when(saleItemRepository.existsByProduct_ProductId(product.getProductId())).thenReturn(true);

            // ASSERTS
            assertThrows(BusinessConflictException.class, () -> productService.deleteProduct(product.getProductId()));
            verify(productRepository, never()).findById(product.getProductId());
            verify(productRepository, never()).delete(product);
        }

        @Test
        void shouldThrowExceptionWhenProductNotFound() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());

            // ASSERTS
            assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(product.getProductId()));
            verify(productRepository, times(1)).findById(product.getProductId());
        }
    }

    @Nested
    class deactivateProduct{
        @Test
        void shouldDeactivateAProductWithSuccessfully(){
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // ACT
            ResponseProductDetailDto responseProductDetailDto = productService.deactivateProduct(product.getProductId(), deactivateProductDto);

            // ASSERT
            assertNotNull(responseProductDetailDto);
            assertFalse(product.isActivity());

        }

        @Test
        void shouldThrowExceptionWhenProductIsAlreadyInactivity() {
            // ARRANGE
            product.setActivityStatus(ActivityStatus.INACTIVITY);
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

            // ASSERTS
            assertThrows(ActivityStatusException.class, () -> productService.deactivateProduct(product.getProductId(), deactivateProductDto));
            verify(productRepository, times(1)).findById(product.getProductId());
            verify(productRepository, never()).save(any(Product.class));
        }
    }

    @Nested
    class activateProduct{
        @Test
        void shouldActivateAProductWithSuccessfully(){
            // ARRANGE
            product.setActivityStatus(ActivityStatus.INACTIVITY);
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(productRepository.save(any(Product.class))).thenReturn(product);

            // ACT
            ResponseProductDetailDto responseProductDetailDto = productService.activateProduct(product.getProductId());

            // ASSERT
            assertNotNull(responseProductDetailDto);
            assertTrue(product.isActivity());
        }

        @Test
        void shouldThrowExceptionWhenProductIsAlreadyActivity() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

            // ASSERTS
            assertThrows(ActivityStatusException.class, () -> productService.activateProduct(product.getProductId()));
            verify(productRepository, times(1)).findById(product.getProductId());
            verify(productRepository, never()).save(any(Product.class));
        }
    }
}