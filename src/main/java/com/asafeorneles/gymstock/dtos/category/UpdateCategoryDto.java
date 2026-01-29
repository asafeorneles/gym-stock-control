package com.asafeorneles.gymstock.dtos.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCategoryDto(
        @NotBlank(message = "The name cannot be empty!")
        @Size(min = 2, max = 100, message = "The name should be between 2 and 100 characters")
        String name,
        @NotBlank(message = "The description cannot be empty!")
        @Size(min = 2, max = 255, message = "The description should be between 2 and 255 characters")
        String description
) {
}
