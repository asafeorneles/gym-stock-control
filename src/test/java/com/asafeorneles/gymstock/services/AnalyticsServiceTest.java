package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.analytics.TopSellingProductsDto;
import com.asafeorneles.gymstock.entities.Product;
import com.asafeorneles.gymstock.entities.SaleItem;
import com.asafeorneles.gymstock.repositories.AnalyticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    AnalyticsRepository analyticsRepository;

    @InjectMocks
    AnalyticsService analyticsService;

    TopSellingProductsDto topSellingProductsDto1;
    TopSellingProductsDto topSellingProductsDto2;
    LocalDate startDate;
    LocalDate endDate;

    LocalDateTime expectedStartDateTime;
    LocalDateTime expectedEndDateTime;

    @BeforeEach
    void setUp() {
        Product product1 = Product.builder()
                .productId(UUID.randomUUID())
                .name("Whey")
                .brand("Growth")
                .price(BigDecimal.valueOf(100.99))
                .costPrice(BigDecimal.valueOf(69.99))
                .build();

        Product product2 = Product.builder()
                .productId(UUID.randomUUID())
                .name("Hipercalórico")
                .brand("Growth")
                .price(BigDecimal.valueOf(100.99))
                .costPrice(BigDecimal.valueOf(69.99))
                .build();

        SaleItem saleItem1 = SaleItem.builder().saleItemId(UUID.randomUUID())
                .product(product1)
                .quantity(2)
                .costPrice(product1.getCostPrice())
                .unityPrice(product1.getPrice())
                .totalPrice(product1.getPrice().multiply(BigDecimal.valueOf(5)))
                .build();

        SaleItem saleItem2 = SaleItem.builder().saleItemId(UUID.randomUUID())
                .product(product2)
                .quantity(6)
                .costPrice(product2.getCostPrice())
                .unityPrice(product2.getPrice())
                .totalPrice(product2.getPrice().multiply(BigDecimal.valueOf(5)))
                .build();

        topSellingProductsDto1 = new TopSellingProductsDto(
                product1.getProductId().toString(),
                product1.getName(),
                BigDecimal.valueOf(saleItem1.getQuantity())
        );

        topSellingProductsDto2 = new TopSellingProductsDto(
                product2.getProductId().toString(),
                product2.getName(),
                BigDecimal.valueOf(saleItem2.getQuantity())
        );
        startDate = LocalDate.of(2025, 3, 1);
        endDate = LocalDate.of(2025, 3, 31);

        expectedStartDateTime = startDate.atStartOfDay();
        expectedEndDateTime = endDate.atTime(23, 59, 59);
    }

    @Nested
    class getTopSellingProducts {
        @Test
        void shouldReturnListOfTopSellingProductsSuccessfully() {
            int limit = 2;
            when(analyticsRepository.findTopSellingProducts(limit)).thenReturn(List.of(topSellingProductsDto1, topSellingProductsDto2));

            List<TopSellingProductsDto> topSellingProducts = analyticsService.getTopSellingProducts(limit);

            assertFalse(topSellingProducts.isEmpty());
            assertEquals(2, topSellingProducts.size());
            assertEquals(topSellingProductsDto1.productName(), topSellingProducts.get(0).productName());
            assertEquals(topSellingProductsDto2.productName(), topSellingProducts.get(1).productName());
            verify(analyticsRepository, times(1)).findTopSellingProducts(limit);
            verify(analyticsRepository).findTopSellingProducts(2);
        }

        @Test
        void shouldReturnEmptyListOfTopSellingProducts() {
            int limit = 2;
            when(analyticsRepository.findTopSellingProducts(limit)).thenReturn(List.of());

            List<TopSellingProductsDto> topSellingProducts = analyticsService.getTopSellingProducts(limit);

            assertTrue(topSellingProducts.isEmpty());
            verify(analyticsRepository, times(1)).findTopSellingProducts(limit);
            verify(analyticsRepository).findTopSellingProducts(2);
        }

        @Test
        void shouldUseDefaultLimitWhenLimitIsNull() {
            when(analyticsRepository.findTopSellingProducts(10))
                    .thenReturn(List.of());

            analyticsService.getTopSellingProducts(null);

            verify(analyticsRepository).findTopSellingProducts(10);
        }
    }

    @Nested
    class getTopSellingProductsByPeriod {
        @Test
        void shouldLimitResultsAndConvertDatesCorrectly() {
            int limit = 2;
            when(analyticsRepository.findTopSellingProductsByPeriod(
                    limit,
                    expectedStartDateTime,
                    expectedEndDateTime
            )).thenReturn(List.of());

            analyticsService.getTopSellingProductsByPeriod(limit, startDate, endDate);

            verify(analyticsRepository).findTopSellingProductsByPeriod(
                    limit,
                    expectedStartDateTime,
                    expectedEndDateTime
            );
        }

        @Test
        void shouldUseDefaultLimitWhenLimitIsNull() {
            when(analyticsRepository.findTopSellingProductsByPeriod(
                    10,
                    expectedStartDateTime,
                    expectedEndDateTime
            )).thenReturn(List.of());

            analyticsService.getTopSellingProductsByPeriod(
                    null, startDate, endDate
            );

            verify(analyticsRepository).findTopSellingProductsByPeriod(
                    10,
                    expectedStartDateTime,
                    expectedEndDateTime
            );
        }

    }
}