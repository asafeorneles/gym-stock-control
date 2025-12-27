package com.asafeorneles.gym_stock_control.mapper;

import com.asafeorneles.gym_stock_control.dtos.coupon.CreateCouponDto;
import com.asafeorneles.gym_stock_control.dtos.coupon.ResponseCouponDto;
import com.asafeorneles.gym_stock_control.entities.Coupon;

public class CouponMapper {
    public static Coupon RespcreateCouponToCoupon(CreateCouponDto createCouponDto){
        return Coupon.builder()
                .code(createCouponDto.code().toUpperCase())
                .description(createCouponDto.description())
                .discountValue(createCouponDto.discountValue())
                .discountType(createCouponDto.discountType())
                .unlimited(createCouponDto.unlimited())
                .quantity(createCouponDto.quantity())
                .activityStatus(createCouponDto.activityStatus())
                .expirationDate(createCouponDto.expirationDate())
                .build();
    }

    public static ResponseCouponDto couponToResponseCoupon(Coupon coupon){
        return new ResponseCouponDto(
                coupon.getCouponId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getDiscountType(),
                coupon.isUnlimited(),
                coupon.getQuantity(),
                coupon.getActivityStatus(),
                coupon.getExpirationDate()
        );
    }
}
