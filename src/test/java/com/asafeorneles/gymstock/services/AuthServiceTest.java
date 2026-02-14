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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    TokenService tokenService;
    @Mock
    UserRepository userRepository;
    @Mock
    RoleRepository roleRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    JwtDecoder jwtDecoder;
    @Mock
    CustomUserDetailsService customUserDetailsService;
    @Mock
    RefreshTokenRedisService refreshTokenRedisService;
    @InjectMocks
    AuthService authService;
    private LoginRequestDto loginRequestDto;
    private RegisterRequestDto registerRequestDto;
    private RefreshTokenRequestDto refreshTokenRequestDto;
    private Role role;
    private User user;
    private String jti;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto("zafin", "123");
        registerRequestDto = new RegisterRequestDto("zafin", "123", "ROLE_BASIC");
        refreshTokenRequestDto = new RefreshTokenRequestDto("old-refresh-accessToken");
        role = new Role(2L, "BASIC");
        user = User.builder().userId(UUID.randomUUID()).username(loginRequestDto.username()).password(passwordEncoder.encode(loginRequestDto.password())).build();
        jti = "test jti";
    }

    @Test
    void shouldLoginSuccessful() {
        String authAccessToken = "test-accessToken";
        String authRefreshToken = "test-refreshToken";

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByUsername(loginRequestDto.username())).thenReturn(Optional.of(user));
        when(tokenService.getAccessToken(authentication)).thenReturn(authAccessToken);
        when(tokenService.getRefreshToken(eq(authentication), anyString())).thenReturn(authRefreshToken);
        when(tokenService.getAccessTokenExpiration()).thenReturn(300L);

        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);

        assertNotNull(loginResponseDto);
        assertEquals(authAccessToken, loginResponseDto.accessToken());
        assertEquals(authRefreshToken, loginResponseDto.refreshToken());
        assertEquals(300L, loginResponseDto.expiresIn());

        verify(refreshTokenRedisService, times(1)).save(anyString(), anyString(), anyLong());
    }

    @Nested
    class register {
        @Test
        void shouldRegisterAUserSuccessfully() {
            when(userRepository.existsByUsername(registerRequestDto.username())).thenReturn(false);
            when(roleRepository.findByName(registerRequestDto.role())).thenReturn(Optional.of(role));
            when(passwordEncoder.encode("123")).thenReturn("encoded-password");

            authService.register(registerRequestDto);

            verify(userRepository).save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            when(userRepository.existsByUsername("zafin")).thenReturn(true);

            assertThrows(BusinessConflictException.class, () -> authService.register(registerRequestDto));
        }

        @Test
        void shouldThrowExceptionWhenRoleNotFound() {
            when(userRepository.existsByUsername("zafin")).thenReturn(false);
            when(roleRepository.findByName("ROLE_BASIC")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> authService.register(registerRequestDto));
        }
    }

    @Nested
    class refreshToken {
        @Test
        void shouldRefreshTokenSuccessfully() {
            String newAccessToken = "test-accessToken";
            String newRefreshToken = "test-refreshToken";

            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(jwt.getId()).thenReturn(jti);
            when(refreshTokenRedisService.existsInRedis(jti)).thenReturn(true);
            when(jwt.getSubject()).thenReturn(user.getUserId().toString());
            when(userRepository.findById(any())).thenReturn(Optional.of(user));

            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("asafe");
            when(userDetails.getAuthorities()).thenReturn(List.of());

            when(customUserDetailsService.loadUserByUsername(user.getUsername())).thenReturn(userDetails);
            when(tokenService.getAccessToken(any())).thenReturn(newAccessToken);
            when(tokenService.getRefreshToken(any(), anyString())).thenReturn(newRefreshToken);
            when(tokenService.getAccessTokenExpiration()).thenReturn(300L);
            when(tokenService.getRefreshTokenExpiration()).thenReturn(28800L);

            LoginResponseDto loginResponseDto = authService.refreshToken(refreshTokenRequestDto);

            assertNotNull(loginResponseDto);
            assertEquals(newRefreshToken, loginResponseDto.refreshToken());
            assertEquals(newAccessToken, loginResponseDto.accessToken());

            verify(refreshTokenRedisService, times(1)).delete(jti);
            verify(refreshTokenRedisService, times(1)).save(anyString(), anyString(), anyLong());
        }

        @Test
        void shouldThrowExceptionWhenTokenTypeIsNotRefresh() {
            String token = "invalid refreshToken";
            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(token)).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("access");

            assertThrows(UnauthorizedException.class, () -> authService.refreshToken(new RefreshTokenRequestDto(token)));

            verify(refreshTokenRedisService, never()).existsInRedis(any());
            verify(refreshTokenRedisService, never()).delete(any());
            verify(userRepository, never()).findById(any());
            verify(jwt, never()).getSubject();
            verify(refreshTokenRedisService, never()).save(anyString(), anyString(), anyLong());
        }

        @Test
        void shouldThrowExceptionWhenRefreshTokenNotFoundInRedis() {
            String token = "tokenTest";

            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(token)).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(jwt.getId()).thenReturn(jti);
            when(refreshTokenRedisService.existsInRedis(jti)).thenReturn(false);

            assertThrows(UnauthorizedException.class, () -> authService.refreshToken(new RefreshTokenRequestDto(token)));
        }
    }

    @Nested
    class logout {
        @Test
        void shouldLogoutSuccessfully() {
            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(jwt.getId()).thenReturn(jti);
            when(refreshTokenRedisService.existsInRedis(jti)).thenReturn(true);

            authService.logout(refreshTokenRequestDto);

            verify(refreshTokenRedisService, times(1)).delete(jti);
        }

        @Test
        void shouldThrowExceptionWhenJwtIsInvalidOrExpired() {
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenThrow(new JwtException("Invalid"));

            assertThrows(UnauthorizedException.class, () -> authService.logout(refreshTokenRequestDto));

            verify(refreshTokenRedisService, never()).existsInRedis(any());
            verify(refreshTokenRedisService, never()).delete(any());
        }

        @Test
        void shouldThrowExceptionWhenTokenTypeIsNotRefresh() {
            String token = "invalid refreshToken";
            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(token)).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("access");

            assertThrows(UnauthorizedException.class, () -> authService.logout(new RefreshTokenRequestDto(token)));

            verify(refreshTokenRedisService, never()).existsInRedis(any());
            verify(refreshTokenRedisService, never()).delete(any());
            verify(userRepository, never()).findById(any());
            verify(jwt, never()).getSubject();
            verify(refreshTokenRedisService, never()).save(anyString(), anyString(), anyLong());
        }
    }
}