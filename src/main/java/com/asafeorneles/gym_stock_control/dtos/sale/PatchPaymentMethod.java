package com.asafeorneles.gym_stock_control.dtos.sale;

import com.asafeorneles.gym_stock_control.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PatchPaymentMethod(
        @NotNull(message = "The payment method of sale items cannot be null!")
        PaymentMethod paymentMethod
) {
}
