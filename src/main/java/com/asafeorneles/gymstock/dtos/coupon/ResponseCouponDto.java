package com.asafeorneles.gymstock.dtos.coupon;

import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResponseCouponDto(
        UUID couponId,
        String code,
        String description,
        BigDecimal discountValue,
        DiscountType discountType,
        boolean isUnlimited,
        int quantity,
        ActivityStatus activityStatus,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime expirationDate
) {
}
