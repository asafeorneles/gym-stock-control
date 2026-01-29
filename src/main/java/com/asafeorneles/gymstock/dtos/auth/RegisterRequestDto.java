package com.asafeorneles.gymstock.dtos.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(
        @NotBlank(message = "The username cannot be empty!")
        @Size(min = 4, max = 100, message = "The username should be between 2 and 100 characters")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "The username must contain only letters, numbers, periods, dashes, or underscores.")
        String username,
        @NotBlank(message = "The password cannot be empty!")
        @Size(min = 4, max = 100, message = "The password should be between 2 and 100 characters")
//        @Pattern(
//                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
//                message = "The password must contain at least one uppercase letter, one lowercase letter, one number, and one special character."
//        )
        String password,
        @NotBlank(message = "The role cannot be empty!")
        String role
) {
}
