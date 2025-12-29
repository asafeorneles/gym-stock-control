package com.asafeorneles.gym_stock_control.dtos.product;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateProductDto(
        @NotBlank(message = "The name cannot be empty!")
        @Size(min = 2, max = 100, message = "The name should be between 2 and 100 characters")
        String name,
        @NotBlank(message = "The brand cannot be empty!")
        @Size(min = 2, max = 100, message = "The brand should be between 2 and 100 characters!")
        String brand,
        @NotBlank(message = "The description cannot be empty!")
        @Size(min = 2, max = 255, message = "The description should be between 2 and 255 characters")
        String description,
        @NotNull(message = "The price is mandatory!")
        @DecimalMin(value = "0.01", message = "The min price is 0.01!")
        BigDecimal price,
        @NotNull(message = "The cost price is mandatory!")
        @DecimalMin(value = "0.01", message = "The min cost price is 0.01!")
        BigDecimal costPrice,
        @NotNull(message = "The category is mandatory!")
        UUID categoryId
) {
}
