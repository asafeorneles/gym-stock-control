package com.asafeorneles.gymstock.dtos.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeactivateProductDto(
        @NotBlank(message = "The reason cannot be empty!")
        @Size(min = 2, max = 255, message = "The reason should be between 2 and 255 characters")
        String reason
){
}
