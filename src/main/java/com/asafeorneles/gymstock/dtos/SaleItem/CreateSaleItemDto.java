package com.asafeorneles.gymstock.dtos.SaleItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateSaleItemDto(
        @NotNull(message = "The productId cannot be null!")
        UUID productId,
        @Min(value = 1, message = "The quantity cannot be less than 1!")
        int quantity
) {
}
