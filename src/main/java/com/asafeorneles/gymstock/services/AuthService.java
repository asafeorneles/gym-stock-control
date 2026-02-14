package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.auth.LoginRequestDto;
import com.asafeorneles.gymstock.dtos.auth.LoginResponseDto;
import com.asafeorneles.gymstock.dtos.auth.RefreshTokenRequestDto;
import com.asafeorneles.gymstock.dtos.auth.RegisterRequestDto;
import com.asafeorneles.gymstock.entities.Role;
import com.asafeorneles.gymstock.entities.User;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.exceptions.UnauthorizedException;
import com.asafeorneles.gymstock.repositories.RoleRepository;
import com.asafeorneles.gymstock.repositories.UserRepository;
import com.asafeorneles.gymstock.security.CustomUserDetailsService;
import com.asafeorneles.gymstock.security.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.UUID;

@Slf4j
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
    RefreshTokenRedisService refreshTokenRedisService;

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

        User user = userRepository.findByUsername(loginRequestDto.username())
                .orElseThrow(() -> new ResourceNotFoundException("User not found by refresh accessToken"));

        String token = tokenService.getAccessToken(authentication);
        String refreshTokenString = generateRefreshToken(authentication, user);

        return new LoginResponseDto(token, refreshTokenString, tokenService.getAccessTokenExpiration());
    }

    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.username())) {
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


    @Transactional
    public LoginResponseDto refreshToken(RefreshTokenRequestDto refreshTokenRequestDto) {
        String oldRefreshTokenString = refreshTokenRequestDto.refreshToken();

        Jwt jwt = validateRefreshToken(oldRefreshTokenString);
        String jti = jwt.getId();

        if (!refreshTokenRedisService.existsInRedis(jti)){
            throw new UnauthorizedException("Refresh Token invalid or expired");
        }

        refreshTokenRedisService.delete(jti);

        User user = userRepository.findById(UUID.fromString(jwt.getSubject()))
                .orElseThrow(() -> new ResourceNotFoundException("User not found in the refresh token"));


        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails.getUsername(),
                null,
                userDetails.getAuthorities()
        );

        String token = tokenService.getAccessToken(authentication);
        String newRefreshTokenString = generateRefreshToken(authentication, user);

        return new LoginResponseDto(token, newRefreshTokenString, tokenService.getAccessTokenExpiration());
    }

    @Transactional
    public void logout(RefreshTokenRequestDto refreshTokenRequestDto) {
        String refreshTokenString = refreshTokenRequestDto.refreshToken();

        Jwt jwt = validateRefreshToken(refreshTokenString);
        String jti = jwt.getId();

        if (!refreshTokenRedisService.existsInRedis(jti)){
            throw new UnauthorizedException("Refresh Token invalid or expired");
        }

        refreshTokenRedisService.delete(jti);
    }

    private Jwt validateRefreshToken(String refreshTokenString) {
        try {
            Jwt jwt = jwtDecoder.decode(refreshTokenString);
            if (!"refresh".equals(jwt.getClaim("type"))) {
                throw new UnauthorizedException("Invalid Token type");
            }
            return jwt;
        } catch (JwtException e) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
    }

    private String generateRefreshToken(Authentication authentication, User user) {
        String jti = UUID.randomUUID().toString();

        String newRefreshTokenString = tokenService.getRefreshToken(authentication, jti);

        refreshTokenRedisService.save(jti, user.getUserId().toString(), tokenService.getRefreshTokenExpiration());
        return newRefreshTokenString;
    }
}
