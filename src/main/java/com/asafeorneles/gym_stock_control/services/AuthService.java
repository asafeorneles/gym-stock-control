package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.auth.LoginRequestDto;
import com.asafeorneles.gym_stock_control.dtos.auth.LoginResponseDto;
import com.asafeorneles.gym_stock_control.dtos.auth.RegisterRequestDto;
import com.asafeorneles.gym_stock_control.entities.Role;
import com.asafeorneles.gym_stock_control.entities.User;
import com.asafeorneles.gym_stock_control.exceptions.BusinessConflictException;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.repositories.RoleRepository;
import com.asafeorneles.gym_stock_control.repositories.UserRepository;
import com.asafeorneles.gym_stock_control.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

       Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.username(),
                        loginRequestDto.password()
                )
        );

        String token = tokenService.generateToken(authentication);

        return new LoginResponseDto(token, tokenService.getTokenExpiresIn());
    }

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.username())){
            throw new BusinessConflictException("Username already in use. Please enter another username.");
        }

        Role role = roleRepository.findByName(registerRequestDto.role())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found by this name: " + registerRequestDto.role()));

        User newUser = User.builder()
                .username(registerRequestDto.username())
                .password(passwordEncoder.encode(registerRequestDto.password()))
                .roles(Set.of(role))
                .build();

        userRepository.save(newUser);
    }
}
