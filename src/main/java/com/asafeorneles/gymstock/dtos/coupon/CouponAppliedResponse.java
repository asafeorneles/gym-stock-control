package com.asafeorneles.gymstock.dtos.coupon;

import java.math.BigDecimal;

public record CouponAppliedResponse(
        String code,
        BigDecimal discountAmount
) {
}
