package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.services.AuthService;
import com.asafeorneles.gym_stock_control.dtos.auth.LoginRequestDto;
import com.asafeorneles.gym_stock_control.dtos.auth.LoginResponseDto;
import com.asafeorneles.gym_stock_control.dtos.auth.RegisterRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDto);
    }

    @PreAuthorize("hasAuthority('user:register')")
    @PostMapping("/register") // Proteger depois
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto registerRequestDto){
        authService.register(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
}
