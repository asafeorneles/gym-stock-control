package com.asafeorneles.gymstock.dtos.sale;

import com.asafeorneles.gymstock.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PatchPaymentMethodDto(
        @NotNull(message = "The payment method of sale items cannot be null!")
        PaymentMethod paymentMethod
) {
}
