package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.SaleItem.CreateSaleItemDto;
import com.asafeorneles.gym_stock_control.dtos.sale.CreateSaleDto;
import com.asafeorneles.gym_stock_control.dtos.sale.PatchPaymentMethodDto;
import com.asafeorneles.gym_stock_control.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gym_stock_control.entities.*;
import com.asafeorneles.gym_stock_control.enums.ActivityStatus;
import com.asafeorneles.gym_stock_control.enums.DiscountType;
import com.asafeorneles.gym_stock_control.enums.PaymentMethod;
import com.asafeorneles.gym_stock_control.exceptions.ActivityStatusException;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.repositories.CouponRepository;
import com.asafeorneles.gym_stock_control.repositories.ProductRepository;
import com.asafeorneles.gym_stock_control.repositories.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    @Mock
    SaleRepository saleRepository;

    @Mock
    ProductRepository productRepository;

    @Mock
    CouponRepository couponRepository;

    @Mock
    CouponService couponService;

    @Mock
    ProductInventoryService productInventoryService;

    @InjectMocks
    SaleService saleService;

    Product product;
    private SaleItem saleItem;
    private Sale sale;
    private Coupon coupon;
    private CreateSaleDto createSaleDto;
    private CreateSaleItemDto createSaleItemDto;
    private PatchPaymentMethodDto patchPaymentMethodDto;
    private Pageable pageable;
    @Captor
    ArgumentCaptor<Sale> saleCaptor;

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
        product.activity();

        saleItem = SaleItem.builder().saleItemId(UUID.randomUUID())
                .product(product)
                .quantity(5)
                .costPrice(product.getCostPrice())
                .unityPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(5)))
                .build();

        coupon = Coupon.builder()
                .couponId(UUID.randomUUID())
                .code("TESTE10")
                .description("teste")
                .activityStatus(ActivityStatus.ACTIVE)
                .discountType(DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.valueOf(10))
                .unlimited(false)
                .quantity(5)
                .build();

        sale = Sale.builder()
                .saleId(UUID.randomUUID())
                .saleItems(List.of(saleItem))
                .paymentMethod(PaymentMethod.PIX)
                .coupon(coupon)
                .build();

        saleItem.setSale(sale);

        sale.calculateTotalPrice();

        createSaleItemDto = new CreateSaleItemDto(product.getProductId(), 5);
        createSaleDto = new CreateSaleDto(List.of(createSaleItemDto), PaymentMethod.PIX, coupon.getCouponId());
        patchPaymentMethodDto = new PatchPaymentMethodDto(PaymentMethod.DEBIT_CARD);
        pageable = PageRequest.of(0, 10);
    }

    @Nested
    class createSale {
        @Test
        void shouldCreateSaleSuccessfully() {
            // ARRANGE
            when(productRepository.findById(any(UUID.class))).thenReturn(Optional.of(saleItem.getProduct()));
            when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));
            when(saleRepository.save(any(Sale.class))).thenReturn(sale);

            // ACT
            ResponseSaleDto responseSaleDto = saleService.createSale(createSaleDto);

            // ASSERT
            verify(saleRepository).save(saleCaptor.capture());
            Sale saleSaved = saleCaptor.getValue();

            assertEquals(1, saleSaved.getSaleItems().size());
            assertEquals(PaymentMethod.PIX, saleSaved.getPaymentMethod());
            assertEquals(sale.getTotalPrice(), saleSaved.getTotalPrice());

            assertNotNull(responseSaleDto);
            assertEquals(sale.getSaleItems().size(), responseSaleDto.saleItems().size());
            assertEquals(sale.getTotalPrice(), responseSaleDto.totalPrice());
            assertEquals(sale.getPaymentMethod(), responseSaleDto.paymentMethod());

            verify(productRepository).findById(createSaleItemDto.productId());

            verify(productInventoryService, times(1)).updateQuantityAfterSale(anyList());
        }

        @Test
        void shouldThrowExceptionWhenProductIsNotFound() {
            when(productRepository.findById(createSaleItemDto.productId()))
                    .thenReturn(Optional.empty());

            // ASSERT
            assertThrows(ResourceNotFoundException.class, () -> saleService.createSale(createSaleDto));

            verify(productInventoryService, never()).updateQuantityAfterSale(any());

            verify(saleRepository, never()).save(any(Sale.class));
        }

        @Test
        void shouldNotPersistSaleWhenInventoryUpdateFails() {
            // ARRANGE
            when(productRepository.findById(createSaleItemDto.productId()))
                    .thenReturn(Optional.of(saleItem.getProduct()));

            doThrow(new RuntimeException("Inventory error"))
                    .when(productInventoryService)
                    .updateQuantityAfterSale(any());

            // ASSERT
            assertThrows(RuntimeException.class, () -> saleService.createSale(createSaleDto));

            verify(saleRepository, never()).save(any());
        }

        @Test
        void shouldThrowAExceptionWhenProductIsNotActivity() {
            // ARRANGE
            product.inactivity("test");
            when(productRepository.findById(product.getProductId())).thenReturn(Optional.of(product));

            // ASSERTS
            assertThrows(ActivityStatusException.class, () -> saleService.createSale(createSaleDto));
        }
    }

    @Nested
    class findSales {
        @Test
        void shouldFindSalesSuccessfully() {
            // ARRANGE
            Page<Sale> salePage = new PageImpl<>(List.of(sale), pageable, 1);
            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(salePage);

            //ACT
            Page<ResponseSaleDto> salesFound = saleService.getAllSales(Specification.unrestricted(), pageable);

            // ASSERT
            assertFalse(salesFound.isEmpty());
            assertEquals(1, salesFound.getTotalElements());

            ResponseSaleDto responseSaleDto = salesFound.getContent().get(0);
            assertEquals(sale.getTotalPrice(), responseSaleDto.totalPrice());
            assertEquals(sale.getPaymentMethod(), responseSaleDto.paymentMethod());

            verify(saleRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        }

        @Test
        void shouldReturnEmptyListWhenSalesIsNotFound() {
            // ARRANGE
            Page<Sale> emptyPage = Page.empty(pageable);
            when(saleRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(emptyPage);

            // ACT
            Page<ResponseSaleDto> salesFound = saleService.getAllSales(Specification.unrestricted(), pageable);

            // ASSERT
            assertTrue(salesFound.isEmpty());
            verify(saleRepository, times(1)).findAll(any(Specification.class), eq(pageable));
        }
    }

    @Nested
    class findSaleById {
        @Test
        void shouldFindSaleByIdSuccessfully() {
            // ARRANGE
            when(saleRepository.findById(sale.getSaleId())).thenReturn(Optional.of(sale));

            //ACT
            ResponseSaleDto saleFound = saleService.getSaleById(sale.getSaleId());

            // ASSERT
            assertEquals(sale.getTotalPrice(), saleFound.totalPrice());
            assertEquals(sale.getPaymentMethod(), saleFound.paymentMethod());
            verify(saleRepository, times(1)).findById(sale.getSaleId());
        }

        @Test
        void shouldThrowExceptionWhenSalesIsNotFound() {
            // ARRANGE
            when(saleRepository.findById(sale.getSaleId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(ResourceNotFoundException.class, () -> saleService.getSaleById(sale.getSaleId()));
            verify(saleRepository, times(1)).findById(sale.getSaleId());
        }
    }

    @Nested
    class deleteSale {
        @Test
        void shouldDeleteSaleSuccessfully() {
            // ARRANGE
            when(saleRepository.findById(sale.getSaleId())).thenReturn(Optional.of(sale));
            doNothing().when(saleRepository).delete(sale);

            // ACT
            saleService.deleteSale(sale.getSaleId());

            // ASSERT
            verify(saleRepository).delete(saleCaptor.capture());
            Sale saleCaptures = saleCaptor.getValue();

            assertEquals(sale.getSaleId(), saleCaptures.getSaleId());
            assertEquals(sale, saleCaptures);
        }

        @Test
        void shouldThrowExceptionWhenSalesIsNotFound() {
            // ARRANGE
            when(saleRepository.findById(sale.getSaleId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(ResourceNotFoundException.class, () -> saleService.deleteSale(sale.getSaleId()));
            verify(saleRepository, times(1)).findById(sale.getSaleId());
            verify(saleRepository, never()).deleteById(sale.getSaleId());
        }

    }

    @Nested
    class updatePaymentMethod {
        @Test
        void shouldUpdateThePaymentMethodSuccessfully() {
            // ARRANGE
            PaymentMethod oldPaymentMethod = sale.getPaymentMethod();
            when(saleRepository.findById(sale.getSaleId())).thenReturn(Optional.of(sale));
            when(saleRepository.save(any(Sale.class))).thenReturn(sale);

            // ACT

            ResponseSaleDto responseSaleDto = saleService.updatePaymentMethod(sale.getSaleId(), patchPaymentMethodDto);

            // ASSERT
            verify(saleRepository).save(saleCaptor.capture());
            Sale saleCaptured = saleCaptor.getValue();

            assertNotNull(responseSaleDto);

            assertEquals(sale.getTotalPrice(), responseSaleDto.totalPrice());
            assertEquals(sale.getTotalPrice(), saleCaptured.getTotalPrice());

            assertEquals(saleCaptured.getPaymentMethod(), patchPaymentMethodDto.paymentMethod());
            assertEquals(responseSaleDto.paymentMethod(), patchPaymentMethodDto.paymentMethod());

            assertNotEquals(oldPaymentMethod, saleCaptured.getPaymentMethod());
            assertNotEquals(oldPaymentMethod, responseSaleDto.paymentMethod());
        }

        @Test
        void shouldThrowExceptionWhenSalesIsNotFound() {
            // ARRANGE
            when(saleRepository.findById(sale.getSaleId())).thenReturn(Optional.empty());

            // ASSERT
            assertThrows(ResourceNotFoundException.class, () -> saleService.updatePaymentMethod(sale.getSaleId(), patchPaymentMethodDto));
            verify(saleRepository, times(1)).findById(sale.getSaleId());
            verify(saleRepository, never()).save(any(Sale.class));
        }

        @Test
        void shouldNotPersistSaleWhenSaleUpdateFails() {
            // ARRANGE
            when(saleRepository.findById(sale.getSaleId())).thenReturn(Optional.of(sale));
            when(saleRepository.save(any(Sale.class))).thenThrow(RuntimeException.class);


            // ASSERT
            assertThrows(RuntimeException.class, () -> saleService.updatePaymentMethod(sale.getSaleId(), patchPaymentMethodDto));
            verify(saleRepository, times(1)).save(any());
        }

    }
}