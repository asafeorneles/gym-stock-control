package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.product.*;
import com.asafeorneles.gymstock.entities.Category;
import com.asafeorneles.gymstock.entities.Product;
import com.asafeorneles.gymstock.entities.ProductInventory;
import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.exceptions.ActivityStatusException;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.CategoryMapper;
import com.asafeorneles.gymstock.mapper.ProductInventoryMapper;
import com.asafeorneles.gymstock.mapper.ProductMapper;
import com.asafeorneles.gymstock.mapper.ProductMapperImpl;
import com.asafeorneles.gymstock.repositories.CategoryRepository;
import com.asafeorneles.gymstock.repositories.ProductRepository;
import com.asafeorneles.gymstock.repositories.SaleItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
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
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private SaleItemRepository saleItemRepository;

    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);
    private final ProductInventoryMapper productInventoryMapper = Mappers.getMapper(ProductInventoryMapper.class);

    @Spy
    private ProductMapper productMapper = new ProductMapperImpl(categoryMapper, productInventoryMapper);


    @InjectMocks
    private ProductService productService;

    private Product product;
    private Product productLowStock;
    private ProductCreateRequest productCreateRequest;
    private ProductUpdateRequest productUpdateRequest;
    private ProductDeactivateRequest productDeactivateRequest;
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

        productCreateRequest = new ProductCreateRequest(
                "Whey",
                "Growth",
                "test",
                BigDecimal.valueOf(100.99),
                BigDecimal.valueOf(69.99),
                category.getCategoryId(),
                35,
                8
        );

        productUpdateRequest = new ProductUpdateRequest(
                "Whey de Baunilha",
                "Growth",
                "test",
                BigDecimal.valueOf(100.99),
                BigDecimal.valueOf(69.99),
                category.getCategoryId()
        );

        productDeactivateRequest = new ProductDeactivateRequest("test");

    }

    @Nested
    class createProduct {
        @Test
        void shouldCreateAProductSuccessfully() {
            // ARRANGE
            when(categoryRepository.findById(productCreateRequest.categoryId())).thenReturn(Optional.of(category));
            when(productRepository.save(any(Product.class))).thenReturn(product);
            when(productRepository.existsByNameAndBrand(productCreateRequest.name(), productCreateRequest.brand())).thenReturn(false);
            // ACT
            ProductDetailResponse productDetailResponse = productService.createProduct(productCreateRequest);
            // ASSERTS
            verify(productRepository).save(productArgumentCaptor.capture());
            verify(productRepository, times(1)).existsByNameAndBrand(productCreateRequest.name(), productCreateRequest.brand());
            Product productCaptured = productArgumentCaptor.getValue();
            assertNotNull(productDetailResponse);

            assertEquals(category, productCaptured.getCategory());

            // ProductCreateRequest -> Product
            assertEquals(productCreateRequest.name(), productCaptured.getName());
            assertEquals(productCreateRequest.brand(), productCaptured.getBrand());
            assertEquals(productCreateRequest.description(), productCaptured.getDescription());
            assertEquals(productCreateRequest.price(), productCaptured.getPrice());
            assertEquals(productCreateRequest.costPrice(), productCaptured.getCostPrice());
            assertEquals(productCreateRequest.categoryId(), productCaptured.getCategory().getCategoryId());
            assertEquals(productCreateRequest.quantity(), productCaptured.getInventory().getQuantity());
            assertEquals(productCreateRequest.lowStockThreshold(), productCaptured.getInventory().getLowStockThreshold());

            // ProductCreateRequest -> ProductDetailResponse
            assertEquals(productCreateRequest.name(), productDetailResponse.name());
            assertEquals(productCreateRequest.brand(), productDetailResponse.brand());
            assertEquals(productCreateRequest.description(), productDetailResponse.description());
            assertEquals(productCreateRequest.price(), productDetailResponse.price());
            assertEquals(productCreateRequest.costPrice(), productDetailResponse.costPrice());
            assertEquals(productCreateRequest.categoryId(), productDetailResponse.category().categoryId());
            assertEquals(productCreateRequest.quantity(), productDetailResponse.inventory().quantity());
            assertEquals(productCreateRequest.lowStockThreshold(), productDetailResponse.inventory().lowStockThreshold());
        }

        @Test
        void shouldThrowAExceptionWhenCategoryDoesNotExist() {
            // ARRANGE
            when(categoryRepository.findById(productCreateRequest.categoryId())).thenThrow(new ErrorResponseException(HttpStatus.NOT_FOUND));

            // ASSERTS
            assertThrows(ErrorResponseException.class, () -> productService.createProduct(productCreateRequest));
            verify(categoryRepository, times(1)).findById(productCreateRequest.categoryId());
        }

        @Test
        void shouldThrowAExceptionWhenProductIsNotCreate() {
            // ARRANGE
            when(productRepository.save(any(Product.class))).thenThrow(new RuntimeException());
            when(categoryRepository.findById(productCreateRequest.categoryId())).thenReturn(Optional.of(category));
            when(productRepository.existsByNameAndBrand(productCreateRequest.name(), productCreateRequest.brand())).thenReturn(false);
            // ASSERTS
            assertThrows(RuntimeException.class, () -> productService.createProduct(productCreateRequest));
            verify(productRepository, times(1)).save(any(Product.class));
            verify(categoryRepository, times(1)).findById(productCreateRequest.categoryId());
            verify(productRepository, times(1)).existsByNameAndBrand(productCreateRequest.name(), productCreateRequest.brand());

        }

        @Test
        void shouldThrowAExceptionWhenPetAlreadyExists() {
            // ARRANGE
            when(categoryRepository.findById(productCreateRequest.categoryId())).thenReturn(Optional.of(category));
            when(productRepository.existsByNameAndBrand(productCreateRequest.name(), productCreateRequest.brand())).thenReturn(true);

            // ASSERTS
            assertThrows(BusinessConflictException.class, () -> productService.createProduct(productCreateRequest));
        }

        @Test
        void shouldThrowAExceptionWhenCategoryIsNotActivity() {
            // ARRANGE
            category.inactivity();
            when(categoryRepository.findById(productCreateRequest.categoryId())).thenReturn(Optional.of(category));

            // ASSERTS
            assertThrows(ActivityStatusException.class, () -> productService.createProduct(productCreateRequest));
        }
    }

    @Nested
    class findProducts {
        @Test
        void shouldFindAllProductsSuccessfully() {
            // ARRANGE
            when(productRepository.findAll(any(Specification.class))).thenReturn(List.of(product));
            // ACT
            List<ProductResponse> productsFound = productService.getAllProducts(Specification.unrestricted());
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
            List<ProductResponse> productsFound = productService.getAllProducts(Specification.unrestricted());

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
            ProductResponse responseProduct = productService.getProductById(product.getProductId());
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
            List<ProductDetailResponse> productsWithLowStockFound = productService.getAllProductsWithLowStock();
            // ASSERT
            verify(productRepository, times(1)).findProductWithLowStock();
            assertFalse(productsWithLowStockFound.isEmpty());
            assertEquals(1, productsWithLowStockFound.size());

            ProductDetailResponse productDetailResponse = productsWithLowStockFound.get(0);

            assertEquals(productLowStock.getProductId(), productDetailResponse.productId());
            assertEquals(productLowStock.getName(), productDetailResponse.name());
            assertEquals(productLowStock.getInventory().getQuantity(), productDetailResponse.inventory().quantity());
            assertEquals(productLowStock.getInventory().getLowStockThreshold(), productDetailResponse.inventory().lowStockThreshold());

        }

        @Test
        void shouldReturnEmptyListWhenProductWithLowStockIsNotFound() {
            // ASSERT
            when(productRepository.findProductWithLowStock()).thenReturn(List.of());

            // ACT
            List<ProductDetailResponse> productsFound = productService.getAllProductsWithLowStock();

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
            ProductDetailResponse productDetailResponse = productService.updateProduct(product.getProductId(), productUpdateRequest);

            // ASSERT
            verify(productRepository).save(productArgumentCaptor.capture());
            Product productCaptured = productArgumentCaptor.getValue();

            assertNotNull(productDetailResponse);
            assertEquals(product.getProductId(), productCaptured.getProductId());

            //Product -> ProductUpdated
            assertEquals(productUpdateRequest.name(), productCaptured.getName());
            assertEquals(productUpdateRequest.brand(), productCaptured.getBrand());
            assertEquals(productUpdateRequest.price(), productCaptured.getPrice());
            assertEquals(productUpdateRequest.costPrice(), productCaptured.getCostPrice());
            assertEquals(productUpdateRequest.categoryId(), productCaptured.getCategory().getCategoryId());

            //UpdateProduct -> RespondeProductDto
            assertEquals(productUpdateRequest.name(), productDetailResponse.name());
            assertEquals(productUpdateRequest.brand(), productDetailResponse.brand());
            assertEquals(productUpdateRequest.price(), productDetailResponse.price());
            assertEquals(productUpdateRequest.costPrice(), productDetailResponse.costPrice());
            assertEquals(productUpdateRequest.categoryId(), productDetailResponse.category().categoryId());
        }

        @Test
        void shouldThrowExceptionWhenProductNotFound() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.empty());

            // ASSERTS
            assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(product.getProductId(), productUpdateRequest));
            verify(productRepository, times(1)).findById(product.getProductId());
        }

        @Test
        void shouldThrowExceptionWhenCategoryDoesNotExist() {
            // ARRANGE
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(categoryRepository.findById((product.getCategory().getCategoryId()))).thenReturn(Optional.empty());

            // ASSERTS
            assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(product.getProductId(), productUpdateRequest));
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
            assertThrows(RuntimeException.class, () -> productService.updateProduct(product.getProductId(), productUpdateRequest));
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
            ProductDetailResponse productDetailResponse = productService.deactivateProduct(product.getProductId(), productDeactivateRequest);

            // ASSERT
            assertNotNull(productDetailResponse);
            assertFalse(product.isActivity());

        }

        @Test
        void shouldThrowExceptionWhenProductIsAlreadyInactivity() {
            // ARRANGE
            product.setActivityStatus(ActivityStatus.INACTIVITY);
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

            // ASSERTS
            assertThrows(ActivityStatusException.class, () -> productService.deactivateProduct(product.getProductId(), productDeactivateRequest));
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
            ProductDetailResponse productDetailResponse = productService.activateProduct(product.getProductId());

            // ASSERT
            assertNotNull(productDetailResponse);
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