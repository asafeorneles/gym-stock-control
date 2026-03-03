package com.asafeorneles.gymstock.dtos.sale;

import com.asafeorneles.gymstock.dtos.SaleItem.SaleItemResponse;
import com.asafeorneles.gymstock.dtos.coupon.CouponAppliedResponse;
import com.asafeorneles.gymstock.dtos.user.UserSoldByResponse;
import com.asafeorneles.gymstock.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID saleId,
        List<SaleItemResponse> saleItems,
        CouponAppliedResponse couponApplied,
        BigDecimal totalPrice,
        PaymentMethod paymentMethod,
        UserSoldByResponse soldBy,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime createdDate
) {
}
