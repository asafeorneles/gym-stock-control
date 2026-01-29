package com.asafeorneles.gymstock.dtos.coupon;

import java.math.BigDecimal;

public record CouponAppliedDto(
        String code,
        BigDecimal discountApplied
) {
}
