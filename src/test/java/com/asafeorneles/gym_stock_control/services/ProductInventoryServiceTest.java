package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryLowStockThresholdDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryQuantityDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventoryDetailDto;
import com.asafeorneles.gym_stock_control.entities.*;
import com.asafeorneles.gym_stock_control.exceptions.BusinessConflictException;
import com.asafeorneles.gym_stock_control.repositories.ProductInventoryRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductInventoryServiceTest {

    @Mock
    ProductInventoryRepository productInventoryRepository;

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductInventoryService productInventoryService;

    private Product product;
    private ProductInventory productInventory;
    private SaleItem saleItem;
    private PatchProductInventoryQuantityDto patchProductInventoryQuantity;
    private PatchProductInventoryLowStockThresholdDto patchProductInventoryLowStockThreshold;

    @Captor
    ArgumentCaptor<ProductInventory> productInventoryArgumentCaptor;

    @BeforeEach
    void setUp() {
        Category category = Category.builder()
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

        productInventory = ProductInventory.builder()
                .productInventoryId(product.getProductId())
                .product(product)
                .quantity(10)
                .lowStockThreshold(5)
                .build();

        product.setInventory(productInventory);

        saleItem = SaleItem.builder().saleItemId(UUID.randomUUID())
                .sale(new Sale())
                .product(product)
                .quantity(5)
                .costPrice(product.getCostPrice())
                .unityPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(5)))
                .build();

        patchProductInventoryQuantity = new PatchProductInventoryQuantityDto(25);
        patchProductInventoryLowStockThreshold = new PatchProductInventoryLowStockThresholdDto(7);

    }

    @Nested
    class findProducts{
        @Test
        void shouldFindProductsInventoriesWithSuccessfully(){
            // ARRANGE
            when(productInventoryRepository.findAll()).thenReturn(List.of(productInventory));

            //ACT
            List<ResponseProductInventoryDetailDto> inventoriesFound = productInventoryService.findProductsInventories();

            //ASSERT
            assertNotNull(inventoriesFound);
            assertEquals(1, inventoriesFound.size());
            assertEquals(productInventory.getProductInventoryId(), inventoriesFound.get(0).productId());
        }

        @Test
        void shouldReturnEmptyListWhenProductsInventoriesIsNotFound(){
            // ARRANGE
            when(productInventoryRepository.findAll()).thenReturn(List.of());

            // ACT
            List<ResponseProductInventoryDetailDto> productsInventoriesFound = productInventoryService.findProductsInventories();

            // ASSERT
            assertTrue(productsInventoriesFound.isEmpty());
            verify(productInventoryRepository, times(1)).findAll();
        }
    }

    @Nested
    class updateQuantity{
        @Test
        void shouldUpdateQuantityOfInventorySuccessfully(){
            // ARRANGE
            product.activity();
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(productInventoryRepository.findById(productInventory.getProductInventoryId())).thenReturn(Optional.of(productInventory));
            when(productInventoryRepository.save(any(ProductInventory.class))).thenReturn(productInventory);

            //ACT
            ResponseProductInventoryDetailDto responseProductInventory = productInventoryService.updateQuantity(productInventory.getProductInventoryId(), patchProductInventoryQuantity);

            //ASSERT
            verify(productInventoryRepository, times(1)).findById(productInventory.getProductInventoryId());
            verify(productInventoryRepository).save(productInventoryArgumentCaptor.capture());
            ProductInventory inventoryCaptured = productInventoryArgumentCaptor.getValue();

            // PatchProductInventoryQuantityDto -> inventorySaved
            assertEquals(patchProductInventoryQuantity.quantity(), inventoryCaptured.getQuantity());

            // PatchProductInventoryQuantityDto -> ResponseProductInventoryDto
            assertEquals(patchProductInventoryQuantity.quantity(), responseProductInventory.quantity());
        }

        @Test
        void shouldThrowExceptionWhenProductsInventoriesQuantityIsNotUpdated(){
            // ARRANGE
            product.activity();
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(productInventoryRepository.findById(productInventory.getProductInventoryId())).thenReturn(Optional.of(productInventory));
            when(productInventoryRepository.save(any(ProductInventory.class))).thenThrow(new RuntimeException());

            // ASSERT
            assertThrows(RuntimeException.class, ()-> productInventoryService.updateQuantity(productInventory.getProductInventoryId(), patchProductInventoryQuantity));
            verify(productInventoryRepository, times(1)).save(any(ProductInventory.class));
        }

        @Test
        void shouldThrowExceptionWhenProductsIsInactive(){
            // ARRANGE
            product.inactivity("teste");
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(productInventoryRepository.findById(productInventory.getProductInventoryId())).thenReturn(Optional.of(productInventory));

            // ASSERT
            assertThrows(BusinessConflictException.class, ()-> productInventoryService.updateQuantity(productInventory.getProductInventoryId(), patchProductInventoryQuantity));
            verify(productInventoryRepository, times(1)).findById(productInventory.getProductInventoryId());
            verify(productRepository, times(1)).findById(product.getProductId());
        }
    }

    @Nested
    class updateLowStockThreshold{
        @Test
        void shouldUpdateLowStockThresholdOfInventorySuccessfully(){
            // ARRANGE
            product.activity();
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(productInventoryRepository.findById(productInventory.getProductInventoryId())).thenReturn(Optional.of(productInventory));
            when(productInventoryRepository.save(any(ProductInventory.class))).thenReturn(productInventory);

            //ACT
            ResponseProductInventoryDetailDto responseProductInventory = productInventoryService.updateLowStockThreshold(productInventory.getProductInventoryId(), patchProductInventoryLowStockThreshold);

            //ASSERT
            verify(productInventoryRepository, times(1)).findById(productInventory.getProductInventoryId());
            verify(productInventoryRepository).save(productInventoryArgumentCaptor.capture());
            ProductInventory inventoryCaptured = productInventoryArgumentCaptor.getValue();

            // PatchProductInventoryQuantityDto -> inventorySaved
            assertEquals(patchProductInventoryLowStockThreshold.lowStockThreshold(), inventoryCaptured.getLowStockThreshold());

            // PatchProductInventoryQuantityDto -> ResponseProductInventoryDto
            assertEquals(patchProductInventoryLowStockThreshold.lowStockThreshold(), responseProductInventory.lowStockThreshold());
        }

        @Test
        void shouldThrowExceptionWhenProductsInventoriesLowStockThresholdIsNotUpdated(){
            // ARRANGE
            product.activity();
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(productInventoryRepository.findById(productInventory.getProductInventoryId())).thenReturn(Optional.of(productInventory));
            when(productInventoryRepository.save(any(ProductInventory.class))).thenThrow(new RuntimeException());

            // ASSERT
            assertThrows(RuntimeException.class, ()-> productInventoryService.updateLowStockThreshold(productInventory.getProductInventoryId(), patchProductInventoryLowStockThreshold));
            verify(productInventoryRepository, times(1)).save(any(ProductInventory.class));
        }

        @Test
        void shouldThrowExceptionWhenProductsIsInactive(){
            // ARRANGE
            product.inactivity("teste");
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));
            when(productInventoryRepository.findById(productInventory.getProductInventoryId())).thenReturn(Optional.of(productInventory));

            // ASSERT
            assertThrows(BusinessConflictException.class, ()-> productInventoryService.updateLowStockThreshold(productInventory.getProductInventoryId(), patchProductInventoryLowStockThreshold));
            verify(productInventoryRepository, times(1)).findById(productInventory.getProductInventoryId());
            verify(productRepository, times(1)).findById(product.getProductId());
        }
    }

    @Nested
    class updateQuantityAfterSale{
        @Test
        void shouldUpdatePaymentTheQuantityOfInventoriesAfterMakingASale(){
            List<SaleItem> saleItems = List.of(saleItem);
            int initialQuantity = saleItem.getProduct().getInventory().getQuantity();
            when(productInventoryRepository.save(any(ProductInventory.class))).thenReturn(productInventory);

            productInventoryService.updateQuantityAfterSale(saleItems);

            verify(productInventoryRepository).save(productInventoryArgumentCaptor.capture());
            ProductInventory inventoryCaptured = productInventoryArgumentCaptor.getValue();

            assertEquals(initialQuantity - saleItem.getQuantity(), inventoryCaptured.getQuantity());
        }
    }
}