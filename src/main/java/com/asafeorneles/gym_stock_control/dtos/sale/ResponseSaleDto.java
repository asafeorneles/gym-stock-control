package com.asafeorneles.gym_stock_control.dtos.sale;

import com.asafeorneles.gym_stock_control.dtos.SaleItem.ResponseSaleItemDto;
import com.asafeorneles.gym_stock_control.enums.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ResponseSaleDto(
        UUID saleId,
        List<ResponseSaleItemDto> saleItems,
        BigDecimal totalPrice,
        PaymentMethod paymentMethod,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime createdDate
) {
}
