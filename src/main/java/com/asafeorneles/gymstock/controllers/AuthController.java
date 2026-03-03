package com.asafeorneles.gymstock.controllers;

import com.asafeorneles.gymstock.dtos.auth.LoginRequest;
import com.asafeorneles.gymstock.dtos.auth.LoginResponse;
import com.asafeorneles.gymstock.dtos.auth.RefreshTokenRequest;
import com.asafeorneles.gymstock.dtos.auth.RegisterRequest;
import com.asafeorneles.gymstock.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth", produces = {"application/json"})
@Tag(name = "Auth")
@RequiredArgsConstructor
public class AuthController {

    final AuthService authService;

    @Operation(summary = "Log in to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password, or inactive user."),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @Operation(summary = "Refresh accessToken")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed  successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user or Unauthorized Exception"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        LoginResponse loginResponse = authService.refreshToken(refreshTokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }

    @Operation(summary = "Logout the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user or Unauthorized Exception"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest){
        authService.logout(refreshTokenRequest);
        return ResponseEntity.status(HttpStatus.OK).body("Logout successful");
    }

    @Operation(summary = "Registers a user in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "409", description = "Username already in use"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('user:register')")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest){
        authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

}
