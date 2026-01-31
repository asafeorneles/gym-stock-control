package com.asafeorneles.gymstock.dtos.sale;

import com.asafeorneles.gymstock.dtos.SaleItem.ResponseSaleItemDto;
import com.asafeorneles.gymstock.dtos.coupon.CouponAppliedDto;
import com.asafeorneles.gymstock.dtos.user.SoldByUserDto;
import com.asafeorneles.gymstock.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ResponseSaleDto(
        UUID saleId,
        List<ResponseSaleItemDto> saleItems,
        CouponAppliedDto couponApplied,
        BigDecimal totalPrice,
        PaymentMethod paymentMethod,
        SoldByUserDto soldBy,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime createdDate
) {
}
