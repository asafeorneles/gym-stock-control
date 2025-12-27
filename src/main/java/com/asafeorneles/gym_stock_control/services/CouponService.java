package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.coupon.CreateCouponDto;
import com.asafeorneles.gym_stock_control.dtos.coupon.ResponseCouponDto;
import com.asafeorneles.gym_stock_control.entities.Coupon;
import com.asafeorneles.gym_stock_control.entities.Sale;
import com.asafeorneles.gym_stock_control.enums.ActivityStatus;
import com.asafeorneles.gym_stock_control.enums.DiscountType;
import com.asafeorneles.gym_stock_control.exceptions.CouponNotFoundException;
import com.asafeorneles.gym_stock_control.exceptions.InvalidCouponException;
import com.asafeorneles.gym_stock_control.mapper.CouponMapper;
import com.asafeorneles.gym_stock_control.repositories.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CouponService {
    @Autowired
    CouponRepository couponRepository;

    @Transactional
    public ResponseCouponDto createCoupon(CreateCouponDto createCouponDto) {
        validateCouponToCreate(createCouponDto);

        Coupon coupon = CouponMapper.RespcreateCouponToCoupon(createCouponDto);
        couponRepository.save(coupon);
        return CouponMapper.couponToResponseCoupon(coupon);
    }

    public List<ResponseCouponDto> getAllCoupons() {
        return couponRepository.findAll().stream().map(CouponMapper::couponToResponseCoupon).toList();
    }

    public void validateCouponToCreate(CreateCouponDto createCouponDto) {
        if (couponRepository.existsByCode(createCouponDto.code())) {
            throw new InvalidCouponException("This coupon already exist!");
        }

        if (!createCouponDto.unlimited() && createCouponDto.quantity() <= 0) {
            throw new InvalidCouponException("Coupon must have quantity when not unlimited");
        }

        if (createCouponDto.discountType() == DiscountType.PERCENTAGE && createCouponDto.discountValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new InvalidCouponException("Percentage discount cannot exceed 100%");
        }

        if (createCouponDto.expirationDate() != null &&
            createCouponDto.expirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Expiration date cannot be in the past");
        }
    }

    public void validateCouponToCreateSale(Coupon coupon) {
        if (coupon.getActivityStatus() == ActivityStatus.INACTIVITY) {
            throw new InvalidCouponException("Coupon inactivity!");
        }

        if (!coupon.isUnlimited() && coupon.getQuantity() <= 0) {
            throw new InvalidCouponException("Coupon sold out!");
        }

        if (coupon.getExpirationDate() != null && coupon.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new InvalidCouponException("Coupon expired!");
        }
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

    public ResponseCouponDto getCouponById(UUID id) {
        return couponRepository.findById(id)
                .map(CouponMapper::couponToResponseCoupon)
                .orElseThrow(() -> new CouponNotFoundException("Coupon not found by id: " + id));

    }
}


