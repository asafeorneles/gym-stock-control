package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.auth.LoginRequestDto;
import com.asafeorneles.gym_stock_control.dtos.auth.LoginResponseDto;
import com.asafeorneles.gym_stock_control.dtos.auth.RefreshTokenRequestDto;
import com.asafeorneles.gym_stock_control.dtos.auth.RegisterRequestDto;
import com.asafeorneles.gym_stock_control.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth", produces = {"application/json"})
@Tag(name = "Auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @Operation(summary = "Log in to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid username or password, or inactive user."),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @Operation(summary = "Refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed  successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user or Unauthorized Exception"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){
        LoginResponseDto loginResponseDto = authService.refreshToken(refreshTokenRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
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
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDto registerRequestDto){
        authService.register(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequestDto refreshTokenRequestDto){
        authService.logout(refreshTokenRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body("Logout successful");
    }

}
