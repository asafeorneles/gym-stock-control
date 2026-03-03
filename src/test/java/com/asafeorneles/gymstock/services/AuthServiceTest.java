package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.auth.LoginRequest;
import com.asafeorneles.gymstock.dtos.auth.LoginResponse;
import com.asafeorneles.gymstock.dtos.auth.RefreshTokenRequest;
import com.asafeorneles.gymstock.dtos.auth.RegisterRequest;
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
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private RefreshTokenRedisService refreshTokenRedisService;

    @InjectMocks
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private Role role;
    private User user;
    private String jti;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest("zafin", "123");
        registerRequest = new RegisterRequest("zafin", "123", "ROLE_BASIC");
        refreshTokenRequest = new RefreshTokenRequest("old-refresh-accessToken");
        role = new Role(2L, "BASIC");
        user = User.builder().userId(UUID.randomUUID()).username(loginRequest.username()).password(passwordEncoder.encode(loginRequest.password())).build();
        jti = "test jti";
    }

    @Test
    void shouldLoginSuccessful() {
        String authAccessToken = "test-accessToken";
        String authRefreshToken = "test-refreshToken";

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(userRepository.findByUsername(loginRequest.username())).thenReturn(Optional.of(user));
        when(tokenService.getAccessToken(authentication)).thenReturn(authAccessToken);
        when(tokenService.getRefreshToken(eq(authentication), anyString())).thenReturn(authRefreshToken);
        when(tokenService.getAccessTokenExpiration()).thenReturn(300L);

        LoginResponse loginResponse = authService.login(loginRequest);

        assertNotNull(loginResponse);
        assertEquals(authAccessToken, loginResponse.accessToken());
        assertEquals(authRefreshToken, loginResponse.refreshToken());
        assertEquals(300L, loginResponse.expiresIn());

        verify(refreshTokenRedisService, times(1)).save(anyString(), anyString(), anyLong());
    }

    @Nested
    class register {
        @Test
        void shouldRegisterAUserSuccessfully() {
            when(userRepository.existsByUsername(registerRequest.username())).thenReturn(false);
            when(roleRepository.findByName(registerRequest.role())).thenReturn(Optional.of(role));
            when(passwordEncoder.encode("123")).thenReturn("encoded-password");

            authService.register(registerRequest);

            verify(userRepository).save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenUsernameAlreadyExists() {
            when(userRepository.existsByUsername("zafin")).thenReturn(true);

            assertThrows(BusinessConflictException.class, () -> authService.register(registerRequest));
        }

        @Test
        void shouldThrowExceptionWhenRoleNotFound() {
            when(userRepository.existsByUsername("zafin")).thenReturn(false);
            when(roleRepository.findByName("ROLE_BASIC")).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> authService.register(registerRequest));
        }
    }

    @Nested
    class refreshToken {
        @Test
        void shouldRefreshTokenSuccessfully() {
            String newAccessToken = "test-accessToken";
            String newRefreshToken = "test-refreshToken";

            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(refreshTokenRequest.refreshToken())).thenReturn(jwt);
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

            LoginResponse loginResponse = authService.refreshToken(refreshTokenRequest);

            assertNotNull(loginResponse);
            assertEquals(newRefreshToken, loginResponse.refreshToken());
            assertEquals(newAccessToken, loginResponse.accessToken());

            verify(refreshTokenRedisService, times(1)).delete(jti);
            verify(refreshTokenRedisService, times(1)).save(anyString(), anyString(), anyLong());
        }

        @Test
        void shouldThrowExceptionWhenTokenTypeIsNotRefresh() {
            String token = "invalid refreshToken";
            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(token)).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("access");

            assertThrows(UnauthorizedException.class, () -> authService.refreshToken(new RefreshTokenRequest(token)));

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

            assertThrows(UnauthorizedException.class, () -> authService.refreshToken(new RefreshTokenRequest(token)));
        }
    }

    @Nested
    class logout {
        @Test
        void shouldLogoutSuccessfully() {
            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(refreshTokenRequest.refreshToken())).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(jwt.getId()).thenReturn(jti);
            when(refreshTokenRedisService.existsInRedis(jti)).thenReturn(true);

            authService.logout(refreshTokenRequest);

            verify(refreshTokenRedisService, times(1)).delete(jti);
        }

        @Test
        void shouldThrowExceptionWhenJwtIsInvalidOrExpired() {
            when(jwtDecoder.decode(refreshTokenRequest.refreshToken())).thenThrow(new JwtException("Invalid"));

            assertThrows(UnauthorizedException.class, () -> authService.logout(refreshTokenRequest));

            verify(refreshTokenRedisService, never()).existsInRedis(any());
            verify(refreshTokenRedisService, never()).delete(any());
        }

        @Test
        void shouldThrowExceptionWhenTokenTypeIsNotRefresh() {
            String token = "invalid refreshToken";
            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(token)).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("access");

            assertThrows(UnauthorizedException.class, () -> authService.logout(new RefreshTokenRequest(token)));

            verify(refreshTokenRedisService, never()).existsInRedis(any());
            verify(refreshTokenRedisService, never()).delete(any());
            verify(userRepository, never()).findById(any());
            verify(jwt, never()).getSubject();
            verify(refreshTokenRedisService, never()).save(anyString(), anyString(), anyLong());
        }
    }
}