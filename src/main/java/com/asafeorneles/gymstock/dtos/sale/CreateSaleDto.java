package com.asafeorneles.gymstock.dtos.sale;

import com.asafeorneles.gymstock.dtos.SaleItem.CreateSaleItemDto;
import com.asafeorneles.gymstock.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateSaleDto(
        @NotNull(message = "The list of sale items cannot be null!")
        @Size(min = 1, message = "The sale must have at least one item")
        @Valid
        List<CreateSaleItemDto> saleItems,
        @NotNull(message = "The payment method of sale items cannot be null!")
        PaymentMethod paymentMethod,
        UUID couponId
) {

}
