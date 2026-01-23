package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.auth.LoginRequestDto;
import com.asafeorneles.gym_stock_control.dtos.auth.LoginResponseDto;
import com.asafeorneles.gym_stock_control.dtos.auth.RefreshTokenRequestDto;
import com.asafeorneles.gym_stock_control.dtos.auth.RegisterRequestDto;
import com.asafeorneles.gym_stock_control.entities.Role;
import com.asafeorneles.gym_stock_control.entities.User;
import com.asafeorneles.gym_stock_control.exceptions.BusinessConflictException;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.exceptions.UnauthorizedException;
import com.asafeorneles.gym_stock_control.repositories.RoleRepository;
import com.asafeorneles.gym_stock_control.repositories.UserRepository;
import com.asafeorneles.gym_stock_control.security.CustomUserDetailsService;
import com.asafeorneles.gym_stock_control.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

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
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {

       Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.username(),
                        loginRequestDto.password()
                )
        );

        String token = tokenService.getAccessToken(authentication);
        String refreshToken = tokenService.getRefreshToken(authentication);

        return new LoginResponseDto(token, refreshToken, tokenService.getTokenExpiresIn());
    }

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.username())){
            throw new BusinessConflictException("Username already in use. Please enter another username.");
        }

        Role role = roleRepository.findByName(registerRequestDto.role())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found by this name: " + registerRequestDto.role()));

        User user = User.builder()
                .username(registerRequestDto.username())
                .password(passwordEncoder.encode(registerRequestDto.password()))
                .roles(Set.of(role))
                .build();

        user.activity();

        userRepository.save(user);
    }

    public LoginResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {

        Jwt jwt = jwtDecoder.decode(refreshTokenRequestDto.refreshToken());
        if (!jwt.getClaim("type").equals("refresh")){
            throw new UnauthorizedException("Invalid refresh token");
        }

        UUID userId = UUID.fromString(jwt.getSubject());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by refresh token"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities()
        );

        String token = tokenService.getAccessToken(authentication);
        String refreshToken = tokenService.getRefreshToken(authentication);

        return new LoginResponseDto(token, refreshToken, tokenService.getTokenExpiresIn());
    }

}
