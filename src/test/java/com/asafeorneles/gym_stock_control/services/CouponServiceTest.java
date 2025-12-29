package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.coupon.CreateCouponDto;
import com.asafeorneles.gym_stock_control.dtos.coupon.ResponseCouponDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDetailDto;
import com.asafeorneles.gym_stock_control.entities.Coupon;
import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.entities.Sale;
import com.asafeorneles.gym_stock_control.enums.ActivityStatus;
import com.asafeorneles.gym_stock_control.enums.DiscountType;
import com.asafeorneles.gym_stock_control.exceptions.ActivityStatusException;
import com.asafeorneles.gym_stock_control.exceptions.BusinessConflictException;
import com.asafeorneles.gym_stock_control.exceptions.InvalidCouponException;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.repositories.CouponRepository;
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
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {
    @Mock
    CouponRepository couponRepository;

    @Mock
    SaleRepository saleRepository;

    @InjectMocks
    CouponService couponService;

    private Coupon coupon;
    private Sale sale;
    private CreateCouponDto createCouponDto;
    @Captor
    ArgumentCaptor<Coupon> couponArgumentCaptor;

    @BeforeEach
    void setUp() {
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

        createCouponDto = new CreateCouponDto(
                "TESTE10",
                "teste",
                BigDecimal.valueOf(10),
                DiscountType.PERCENTAGE,
                false,
                5,
                ActivityStatus.ACTIVE,
                null
        );

        sale = Sale.builder()
                .totalPrice(BigDecimal.valueOf(200))
                .coupon(coupon)
                .build();
    }

    @Nested
    class createCoupon {
        @Test
        void shouldCreateACouponWithSuccessfully() {
            when(couponRepository.existsByCode(coupon.getCode())).thenReturn(false);
            when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

            ResponseCouponDto responseCouponDto = couponService.createCoupon(createCouponDto);

            verify(couponRepository).save(couponArgumentCaptor.capture());
            Coupon coupon = couponArgumentCaptor.getValue();

            assertNotNull(responseCouponDto);
            assertEquals(createCouponDto.code(), coupon.getCode());
            assertEquals(createCouponDto.description(), coupon.getDescription());
            assertEquals(createCouponDto.activityStatus(), coupon.getActivityStatus());
            assertEquals(createCouponDto.unlimited(), coupon.isUnlimited());
            assertEquals(createCouponDto.quantity(), coupon.getQuantity());
            assertEquals(createCouponDto.discountValue(), coupon.getDiscountValue());
            assertEquals(createCouponDto.discountType(), coupon.getDiscountType());
            assertEquals(createCouponDto.expirationDate(), coupon.getExpirationDate());

            assertEquals(createCouponDto.code(), responseCouponDto.code());
            assertEquals(createCouponDto.description(), responseCouponDto.description());
            assertEquals(createCouponDto.activityStatus(), responseCouponDto.activityStatus());
            assertEquals(createCouponDto.unlimited(), responseCouponDto.isUnlimited());
            assertEquals(createCouponDto.quantity(), responseCouponDto.quantity());
            assertEquals(createCouponDto.discountValue(), responseCouponDto.discountValue());
            assertEquals(createCouponDto.discountType(), responseCouponDto.discountType());
            assertEquals(createCouponDto.expirationDate(), responseCouponDto.expirationDate());
        }

        @Test
        void shouldThrowAExceptionWhenCouponIsNotCreate() {
            // ARRANGE
            when(couponRepository.save(any(Coupon.class))).thenThrow(new RuntimeException());

            // ASSERTS
            assertThrows(RuntimeException.class, () -> couponService.createCoupon(createCouponDto));
            verify(couponRepository, times(1)).save(any(Coupon.class));

        }
    }

    @Nested
    class validateCouponToCreate {
        @Test
        void shouldThrowExceptionWhenCouponAlreadyExist() {
            when(couponRepository.existsByCode(createCouponDto.code())).thenReturn(true);

            assertThrows(BusinessConflictException.class, () -> couponService.validateCouponToCreate(createCouponDto));
            verify(couponRepository, times(1)).existsByCode(createCouponDto.code());
        }

        @Test
        void shouldThrowExceptionWhenCouponQuantityIsLessOrEqualZero() {
            CreateCouponDto createCouponDtoLowQuantity = new CreateCouponDto(
                    "TESTE10",
                    "teste",
                    BigDecimal.valueOf(10),
                    DiscountType.PERCENTAGE,
                    false,
                    -2,
                    ActivityStatus.ACTIVE,
                    null
            );

            assertThrows(InvalidCouponException.class, () -> couponService.validateCouponToCreate(createCouponDtoLowQuantity));
            verify(couponRepository, times(1)).existsByCode(createCouponDtoLowQuantity.code());
        }

        @Test
        void shouldThrowExceptionWhenPercentageCouponExceed100() {
            CreateCouponDto createCouponDtoValueExceeded = new CreateCouponDto(
                    "TESTE10",
                    "teste",
                    BigDecimal.valueOf(105),
                    DiscountType.PERCENTAGE,
                    false,
                    5,
                    ActivityStatus.ACTIVE,
                    null
            );

            assertThrows(InvalidCouponException.class, () -> couponService.validateCouponToCreate(createCouponDtoValueExceeded));
            verify(couponRepository, times(1)).existsByCode(createCouponDtoValueExceeded.code());
        }

        @Test
        void shouldThrowExceptionWhenExpirationDateIsInThePast() {
            CreateCouponDto createCouponDtoExpirationDate = new CreateCouponDto(
                    "TESTE10",
                    "teste",
                    BigDecimal.valueOf(105),
                    DiscountType.PERCENTAGE,
                    false,
                    5,
                    ActivityStatus.ACTIVE,
                    LocalDateTime.of(2020, Month.DECEMBER, 10, 0, 0)
            );

            assertThrows(InvalidCouponException.class, () -> couponService.validateCouponToCreate(createCouponDtoExpirationDate));
            verify(couponRepository, times(1)).existsByCode(createCouponDtoExpirationDate.code());
        }
    }

    @Nested
    class validateCouponToCreateSale {
        @Test
        void shouldThrowExceptionWhenCouponIsInactivity() {
            coupon.setActivityStatus(ActivityStatus.INACTIVITY);

            assertThrows(ActivityStatusException.class, () -> couponService.validateCouponToCreateSale(coupon));
        }

        @Test
        void shouldThrowExceptionWhenCouponSoldOut() {
            coupon.setQuantity(0);

            assertThrows(InvalidCouponException.class, () -> couponService.validateCouponToCreateSale(coupon));
        }

        @Test
        void shouldThrowExceptionWhenCouponExpired() {
            coupon.setExpirationDate(LocalDateTime.of(2025, 12, 10, 0, 0));

            assertThrows(InvalidCouponException.class, () -> couponService.validateCouponToCreateSale(coupon));
        }
    }

    @Nested
    class getAllCoupons {
        @Test
        void shouldGetAllCouponsWithSuccessfully(){
            when(couponRepository.findAll(any(Specification.class))).thenReturn(List.of(coupon));

            List<ResponseCouponDto> couponsFound = couponService.getAllCoupons(Specification.unrestricted());

            assertFalse(couponsFound.isEmpty());
            assertEquals(1, couponsFound.size());
            assertEquals(coupon.getCode(), couponsFound.get(0).code());
            assertEquals(coupon.getDescription(), couponsFound.get(0).description());
            assertEquals(coupon.getActivityStatus(), couponsFound.get(0).activityStatus());
            assertEquals(coupon.isUnlimited(), couponsFound.get(0).isUnlimited());
            assertEquals(coupon.getQuantity(), couponsFound.get(0).quantity());
            assertEquals(coupon.getDiscountValue(), couponsFound.get(0).discountValue());
            assertEquals(coupon.getDiscountType(), couponsFound.get(0).discountType());
            assertEquals(coupon.getExpirationDate(), couponsFound.get(0).expirationDate());
            verify(couponRepository, times(1)).findAll(any(Specification.class));
        }

        @Test
        void shouldThrowAExceptionWhenCouponIsNotFound() {
            when(couponRepository.findAll(any(Specification.class))).thenThrow(ResourceNotFoundException.class);

            assertThrows(ResourceNotFoundException.class, () -> couponService.getAllCoupons(Specification.unrestricted()));
            verify(couponRepository, times(1)).findAll(any(Specification.class));
        }

    }

    @Nested
    class getCouponById {
        @Test
        void shouldGetCouponsByIdWithSuccessfully(){
            when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));

            ResponseCouponDto responseCouponDto = couponService.getCouponById(coupon.getCouponId());

            assertNotNull(responseCouponDto);
            assertEquals(coupon.getCode(), responseCouponDto.code());
            assertEquals(coupon.getDescription(), responseCouponDto.description());
            assertEquals(coupon.getActivityStatus(), responseCouponDto.activityStatus());
            assertEquals(coupon.isUnlimited(), responseCouponDto.isUnlimited());
            assertEquals(coupon.getQuantity(), responseCouponDto.quantity());
            assertEquals(coupon.getDiscountValue(), responseCouponDto.discountValue());
            assertEquals(coupon.getDiscountType(), responseCouponDto.discountType());
            assertEquals(coupon.getExpirationDate(), responseCouponDto.expirationDate());
            verify(couponRepository, times(1)).findById(coupon.getCouponId());
        }

        @Test
        void shouldThrowAExceptionWhenCouponIsNotFound() {
            when(couponRepository.findById(coupon.getCouponId())).thenThrow(ResourceNotFoundException.class);

            assertThrows(ResourceNotFoundException.class, () -> couponService.getCouponById(coupon.getCouponId()));
            verify(couponRepository, times(1)).findById(coupon.getCouponId());
        }
    }

    @Nested
    class deleteCoupon {
        @Test
        void shouldDeleteAPetWithSuccessfully() {
            when(saleRepository.existsByCoupon_CouponId(coupon.getCouponId())).thenReturn(false);
            when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));
            doNothing().when(couponRepository).delete(coupon);

            couponService.deleteCoupon(coupon.getCouponId());

            verify(saleRepository, times(1)).existsByCoupon_CouponId(coupon.getCouponId());
            verify(couponRepository, times(1)).findById(coupon.getCouponId());
            verify(couponRepository).delete(couponArgumentCaptor.capture());
            Coupon couponCaptured = couponArgumentCaptor.getValue();

            assertEquals(coupon.getCouponId(), couponCaptured.getCouponId());
            assertEquals(coupon, couponCaptured);
        }

        @Test
        void shouldThrowExceptionWhenCouponHasAlreadyUsedInASale() {
            when(saleRepository.existsByCoupon_CouponId(coupon.getCouponId())).thenReturn(true);

            assertThrows(BusinessConflictException.class, () -> couponService.deleteCoupon(coupon.getCouponId()));

            verify(couponRepository, never()).findById(coupon.getCouponId());
            verify(couponRepository, never()).delete(any(Coupon.class));
        }

        @Test
        void shouldThrowAExceptionWhenCouponIsNotFound() {
            when(couponRepository.findById(coupon.getCouponId())).thenThrow(ResourceNotFoundException.class);

            assertThrows(ResourceNotFoundException.class, () -> couponService.deleteCoupon(coupon.getCouponId()));
            verify(couponRepository, never()).delete(any(Coupon.class));
        }
    }

    @Nested
    class deactivateCoupon{
        @Test
        void shouldDeactivateACouponWithSuccessfully(){
            // ARRANGE
            when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));
            when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

            // ACT
            ResponseCouponDto responseCouponDto = couponService.deactivateCoupon(coupon.getCouponId());

            // ASSERT
            assertNotNull(responseCouponDto);
            assertFalse(coupon.isActivity());

        }

        @Test
        void shouldThrowExceptionWhenCouponIsAlreadyInactivity() {
            // ARRANGE
            coupon.setActivityStatus(ActivityStatus.INACTIVITY);
            when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));

            // ASSERTS
            assertThrows(ActivityStatusException.class, () -> couponService.deactivateCoupon(coupon.getCouponId()));
            verify(couponRepository, times(1)).findById(coupon.getCouponId());
            verify(couponRepository, never()).save(any(Coupon.class));
        }
    }

    @Nested
    class activityCoupon{
        @Test
        void shouldDeactivateAProductWithSuccessfully(){
            // ARRANGE
            when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));
            when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

            // ACT
            ResponseCouponDto responseCouponDto = couponService.deactivateCoupon(coupon.getCouponId());

            // ASSERT
            assertNotNull(responseCouponDto);
            assertFalse(coupon.isActivity());

        }

        @Test
        void shouldThrowExceptionWhenProductIsAlreadyInactivity() {
            // ARRANGE
            coupon.setActivityStatus(ActivityStatus.INACTIVITY);
            when(couponRepository.findById(coupon.getCouponId())).thenReturn(Optional.of(coupon));

            // ASSERTS
            assertThrows(ActivityStatusException.class, () -> couponService.deactivateCoupon(coupon.getCouponId()));
            verify(couponRepository, times(1)).findById(coupon.getCouponId());
            verify(couponRepository, never()).save(any(Coupon.class));
        }
    }

    @Nested
    class applyCoupon{
        @Test
        void shouldApplyCouponInSaleCorrectly(){
            couponService.applyCoupon(sale);

            assertEquals(BigDecimal.valueOf(180), sale.getTotalPrice());
            assertEquals( BigDecimal.valueOf(20), sale.getDiscountAmount());
            assertEquals(4, coupon.getQuantity());
            verify(couponRepository, times(1)).save(coupon);
        }
    }

    @Nested
    class calculateDiscount{
        @Test
        void shouldCalculatePercentageDiscountCorrectly(){
            BigDecimal discount = couponService.calculateDiscount(sale);
            assertEquals(discount, BigDecimal.valueOf(20));
        }

        @Test
        void shouldCalculateFixedDiscountCorrectly(){
            coupon.setDiscountType(DiscountType.FIXED);
            BigDecimal discount = couponService.calculateDiscount(sale);
            assertEquals(discount, BigDecimal.valueOf(10));
        }

    }

    @Nested
    class decreaseCouponQuantity{
        @Test
        void shouldDecreaseCouponQuantityCorrectly(){
            couponService.decreaseCouponQuantity(coupon);
            assertEquals(4, coupon.getQuantity());
            verify(couponRepository, times(1)).save(coupon);
        }
    }
}