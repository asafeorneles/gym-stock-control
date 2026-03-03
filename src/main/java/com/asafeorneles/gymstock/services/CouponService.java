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
import com.asafeorneles.gymstock.repositories.CouponRepository;
import com.asafeorneles.gymstock.repositories.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CouponService {
    final CouponRepository couponRepository;

    final SaleRepository saleRepository;

    final CouponMapper couponMapper;

    @Transactional
    public CouponResponse createCoupon(CreateCouponRequest createCouponRequest) {
        validateCouponToCreate(createCouponRequest);

        Coupon coupon = couponMapper.toEntity(createCouponRequest);
        couponRepository.save(coupon);
        return couponMapper.toResponse(coupon);
    }

    public List<CouponResponse> getAllCoupons(Specification<Coupon> specification) {
        return couponRepository.findAll(specification).stream().map(couponMapper::toResponse).toList();
    }

    public void validateCouponToCreate(CreateCouponRequest createCouponRequest) {
        if (couponRepository.existsByCode(createCouponRequest.code())) {
            throw new BusinessConflictException("This coupon already exist!");
        }

        if (!createCouponRequest.unlimited() && createCouponRequest.quantity() <= 0) {
            throw new InvalidCouponException("Coupon must have quantity when not unlimited");
        }

        if (createCouponRequest.discountType() == DiscountType.PERCENTAGE && createCouponRequest.discountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidCouponException("Percentage discount cannot exceed 100%");
        }

        if (createCouponRequest.expirationDate() != null &&
            createCouponRequest.expirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Expiration date cannot be in the past");
        }
    }

    public void validateCouponToCreateSale(Coupon coupon) {
        if (coupon.getActivityStatus() == ActivityStatus.INACTIVITY) {
            throw new ActivityStatusException("Coupon inactivity!");
        }

        if (!coupon.isUnlimited() && coupon.getQuantity() <= 0) {
            throw new InvalidCouponException("Coupon sold out!");
        }

        if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Coupon expired!");
        }
    }

    public CouponResponse getCouponById(UUID id) {
        return couponRepository.findById(id)
                .map(couponMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found by id: " + id));

    }

    @Transactional
    public void deleteCoupon(UUID id) {
        if (saleRepository.existsByCoupon_CouponId(id)){
            throw new BusinessConflictException("This coupon has already been used in a sale. Please use the deactivate option.");
        }

        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found by id: " + id));

        couponRepository.delete(coupon);
    }

    public CouponResponse deactivateCoupon(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found by id: " + id));

        coupon.inactivity();
        couponRepository.save(coupon);
        return couponMapper.toResponse(coupon);
    }

    public CouponResponse activateCoupon(UUID id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found by id: " + id));

        coupon.activity();
        couponRepository.save(coupon);
        return couponMapper.toResponse(coupon);
    }


    public void applyCoupon(Sale sale) {
        BigDecimal discount = calculateDiscount(sale);
        sale.setTotalPrice(sale.getTotalPrice().subtract(discount));
        sale.setDiscountAmount(discount);
        decreaseCouponQuantity(sale.getCoupon());
    }

    public BigDecimal calculateDiscount(Sale sale) {
        if (sale.getCoupon().getDiscountType() == DiscountType.PERCENTAGE) {
            return sale.getTotalPrice()
                    .multiply(sale.getCoupon().getDiscountValue())
                    .divide(BigDecimal.valueOf(100));
        } else {
            return sale.getCoupon().getDiscountValue();
        }
    }

    public void decreaseCouponQuantity(Coupon coupon) {
        int currentCouponQuantity = coupon.getQuantity();
        coupon.setQuantity(currentCouponQuantity - 1);
        couponRepository.save(coupon);
    }


}


