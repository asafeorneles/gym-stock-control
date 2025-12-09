package com.asafeorneles.gym_stock_control.dtos.category;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryDto(
        @NotBlank(message = "The name cannot be empty!")
        String name,
        @NotBlank(message = "The description cannot be empty!")
        String description
) {
}
