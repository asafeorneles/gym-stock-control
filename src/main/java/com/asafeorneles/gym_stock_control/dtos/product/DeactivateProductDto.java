package com.asafeorneles.gym_stock_control.dtos.product;

import jakarta.validation.constraints.NotBlank;

public record DeactivateProductDto(
        @NotBlank(message = "The reason cannot be empty!")
        String reason
){
}
