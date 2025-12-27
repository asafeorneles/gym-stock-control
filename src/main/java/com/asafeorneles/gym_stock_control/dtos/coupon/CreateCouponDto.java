package com.asafeorneles.gym_stock_control.dtos.coupon;

import com.asafeorneles.gym_stock_control.enums.ActivityStatus;
import com.asafeorneles.gym_stock_control.enums.DiscountType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateCouponDto(
        @NotBlank(message = "The code cannot be empty!")
        String code,
        @NotBlank(message = "The description cannot be empty!")
        String description,
        @NotNull(message = "The discountValue cannot be empty!")
        @DecimalMin(value = "1", message = "The min value is 1!")
        BigDecimal discountValue,
        @NotNull(message = "The discountType cannot be empty!")
        DiscountType discountType,
        boolean unlimited,
        @Min(value = 1, message = "The min quantity is 1!")
        int quantity,
        @NotNull(message = "The activityStatus cannot be empty!")
        ActivityStatus activityStatus,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime expirationDate
) {
}
