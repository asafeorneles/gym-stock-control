package com.asafeorneles.gym_stock_control.dtos.coupon;

import java.math.BigDecimal;

public record CouponAppliedDto(
        String code,
        BigDecimal discountApplied
) {
}
