package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.coupon.CouponResponse;
import com.asafeorneles.gymstock.dtos.coupon.CreateCouponRequest;
import com.asafeorneles.gymstock.entities.Coupon;
import com.asafeorneles.gymstock.entities.Sale;
import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.enums.DiscountType;
import com.asafeorneles.gymstock.exceptions.ActivityStatusException;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.InvalidCouponException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.CouponMapper;
import com.asafeorneles.gymstock.mapper.CouponMapperImpl;
import com.asafeorneles.gymstock.repositories.CouponRepository;
import com.asafeorneles.gymstock.repositories.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
    private CouponRepository couponRepository;

    @Mock
    private SaleRepository saleRepository;

    @InjectMocks
    private CouponService couponService;

    @Spy
    private CouponMapper couponMapper = new CouponMapperImpl();

    private Coupon coupon;
    private Sale sale;
    private CreateCouponRequest createCouponRequest;
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

        createCouponRequest = new CreateCouponRequest(
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

            CouponResponse couponResponse = couponService.createCoupon(createCouponRequest);

            verify(couponRepository).save(couponArgumentCaptor.capture());
            Coupon coupon = couponArgumentCaptor.getValue();

            assertNotNull(couponResponse);
            assertEquals(createCouponRequest.code(), coupon.getCode());
            assertEquals(createCouponRequest.description(), coupon.getDescription());
            assertEquals(createCouponRequest.activityStatus(), coupon.getActivityStatus());
            assertEquals(createCouponRequest.unlimited(), coupon.isUnlimited());
            assertEquals(createCouponRequest.quantity(), coupon.getQuantity());
            assertEquals(createCouponRequest.discountValue(), coupon.getDiscountValue());
            assertEquals(createCouponRequest.discountType(), coupon.getDiscountType());
            assertEquals(createCouponRequest.expirationDate(), coupon.getExpirationDate());

            assertEquals(createCouponRequest.code(), couponResponse.code());
            assertEquals(createCouponRequest.description(), couponResponse.description());
            assertEquals(createCouponRequest.activityStatus(), couponResponse.activityStatus());
            assertEquals(createCouponRequest.unlimited(), couponResponse.unlimited());
            assertEquals(createCouponRequest.quantity(), couponResponse.quantity());
            assertEquals(createCouponRequest.discountValue(), couponResponse.discountValue());
            assertEquals(createCouponRequest.discountType(), couponResponse.discountType());
            assertEquals(createCouponRequest.expirationDate(), couponResponse.expirationDate());
        }

        @Test
        void shouldThrowAExceptionWhenCouponIsNotCreate() {
            // ARRANGE
            when(couponRepository.save(any(Coupon.class))).thenThrow(new RuntimeException());

            // ASSERTS
            assertThrows(RuntimeException.class, () -> couponService.createCoupon(createCouponRequest));
            verify(couponRepository, times(1)).save(any(Coupon.class));

        }
    }

    @Nested
    class validateCouponToCreate {
        @Test
        void shouldThrowExceptionWhenCouponAlreadyExist() {
            when(couponRepository.existsByCode(createCouponRequest.code())).thenReturn(true);

            assertThrows(BusinessConflictException.class, () -> couponService.validateCouponToCreate(createCouponRequest));
            verify(couponRepository, times(1)).existsByCode(createCouponRequest.code());
        }

        @Test
        void shouldThrowExceptionWhenCouponQuantityIsLessOrEqualZero() {
            CreateCouponRequest createCouponRequestLowQuantity = new CreateCouponRequest(
                    "TESTE10",
                    "teste",
                    BigDecimal.valueOf(10),
                    DiscountType.PERCENTAGE,
                    false,
                    -2,
                    ActivityStatus.ACTIVE,
                    null
            );

            assertThrows(InvalidCouponException.class, () -> couponService.validateCouponToCreate(createCouponRequestLowQuantity));
            verify(couponRepository, times(1)).existsByCode(createCouponRequestLowQuantity.code());
        }

        @Test
        void shouldThrowExceptionWhenPercentageCouponExceed100() {
            CreateCouponRequest createCouponRequestValueExceeded = new CreateCouponRequest(
                    "TESTE10",
                    "teste",
                    BigDecimal.valueOf(105),
                    DiscountType.PERCENTAGE,
                    false,
                    5,
                    ActivityStatus.ACTIVE,
                    null
            );

            assertThrows(InvalidCouponException.class, () -> couponService.validateCouponToCreate(createCouponRequestValueExceeded));
            verify(couponRepository, times(1)).existsByCode(createCouponRequestValueExceeded.code());
        }

        @Test
        void shouldThrowExceptionWhenExpirationDateIsInThePast() {
            CreateCouponRequest createCouponRequestExpirationDate = new CreateCouponRequest(
                    "TESTE10",
                    "teste",
                    BigDecimal.valueOf(105),
                    DiscountType.PERCENTAGE,
                    false,
                    5,
                    ActivityStatus.ACTIVE,
                    LocalDateTime.of(2020, Month.DECEMBER, 10, 0, 0)
            );

            assertThrows(InvalidCouponException.class, () -> couponService.validateCouponToCreate(createCouponRequestExpirationDate));
            verify(couponRepository, times(1)).existsByCode(createCouponRequestExpirationDate.code());
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

            List<CouponResponse> couponsFound = couponService.getAllCoupons(Specification.unrestricted());

            assertFalse(couponsFound.isEmpty());
            assertEquals(1, couponsFound.size());
            assertEquals(coupon.getCode(), couponsFound.get(0).code());
            assertEquals(coupon.getDescription(), couponsFound.get(0).description());
            assertEquals(coupon.getActivityStatus(), couponsFound.get(0).activityStatus());
            assertEquals(coupon.isUnlimited(), couponsFound.get(0).unlimited());
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

            CouponResponse couponResponse = couponService.getCouponById(coupon.getCouponId());

            assertNotNull(couponResponse);
            assertEquals(coupon.getCode(), couponResponse.code());
            assertEquals(coupon.getDescription(), couponResponse.description());
            assertEquals(coupon.getActivityStatus(), couponResponse.activityStatus());
            assertEquals(coupon.isUnlimited(), couponResponse.unlimited());
            assertEquals(coupon.getQuantity(), couponResponse.quantity());
            assertEquals(coupon.getDiscountValue(), couponResponse.discountValue());
            assertEquals(coupon.getDiscountType(), couponResponse.discountType());
            assertEquals(coupon.getExpirationDate(), couponResponse.expirationDate());
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
            CouponResponse couponResponse = couponService.deactivateCoupon(coupon.getCouponId());

            // ASSERT
            assertNotNull(couponResponse);
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
            CouponResponse couponResponse = couponService.deactivateCoupon(coupon.getCouponId());

            // ASSERT
            assertNotNull(couponResponse);
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