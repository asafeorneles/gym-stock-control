package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.auth.LoginRequestDto;
import com.asafeorneles.gymstock.dtos.auth.LoginResponseDto;
import com.asafeorneles.gymstock.dtos.auth.RefreshTokenRequestDto;
import com.asafeorneles.gymstock.dtos.auth.RegisterRequestDto;
import com.asafeorneles.gymstock.entities.RefreshToken;
import com.asafeorneles.gymstock.entities.Role;
import com.asafeorneles.gymstock.entities.User;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.exceptions.UnauthorizedException;
import com.asafeorneles.gymstock.repositories.RefreshTokenRepository;
import com.asafeorneles.gymstock.repositories.RoleRepository;
import com.asafeorneles.gymstock.repositories.UserRepository;
import com.asafeorneles.gymstock.security.CustomUserDetailsService;
import com.asafeorneles.gymstock.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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

import java.time.Instant;
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
    RefreshTokenRepository refreshTokenRepository;

    @Mock
    JwtDecoder jwtDecoder;

    @Mock
    CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    AuthService authService;

    private LoginRequestDto loginRequestDto;
    private RegisterRequestDto registerRequestDto;
    private RefreshTokenRequestDto refreshTokenRequestDto;
    private Role role;
    private User user;
    private RefreshToken oldRefreshToken;

    @Captor
    ArgumentCaptor<RefreshToken> refreshTokenArgumentCaptor;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto("zafin", "123");
        registerRequestDto = new RegisterRequestDto("zafin", "123", "ROLE_BASIC");
        refreshTokenRequestDto = new RefreshTokenRequestDto("old-refresh-accessToken");
        role = new Role(2L, "BASIC");
        user = User.builder()
                .userId(UUID.randomUUID())
                .username(loginRequestDto.username())
                .password(passwordEncoder.encode(loginRequestDto.password()))
                .build();

        oldRefreshToken = RefreshToken.builder()
                .revoked(false)
                .expiresDate(Instant.now().plusSeconds(tokenService.getRefreshTokenExpiration()))
                .token(refreshTokenRequestDto.refreshToken())
                .user(user)
                .build();
    }

    @Test
    void shouldLoginSuccessful() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.getAccessToken(authentication)).thenReturn("accessToken-teste");
        when(tokenService.getRefreshToken(authentication)).thenReturn("refresh-accessToken-teste");
        when(userRepository.findByUsername(loginRequestDto.username())).thenReturn(Optional.of(user));
        when(tokenService.getAccessTokenExpiration()).thenReturn(300L);
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(any(RefreshToken.class));

        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);

        assertNotNull(loginResponseDto);
        assertEquals("accessToken-teste", loginResponseDto.accessToken());
        assertEquals("refresh-accessToken-teste", loginResponseDto.refreshToken());
        assertEquals(300L, loginResponseDto.expiresIn());
        verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
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
            when(userRepository.existsByUsername("zafin"))
                    .thenReturn(true);

            assertThrows(BusinessConflictException.class, () ->
                    authService.register(registerRequestDto)
            );
        }

        @Test
        void shouldThrowExceptionWhenRoleNotFound() {
            when(userRepository.existsByUsername("zafin"))
                    .thenReturn(false);

            when(roleRepository.findByName("ROLE_BASIC"))
                    .thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () ->
                    authService.register(registerRequestDto)
            );
        }
    }

    @Nested
    class refreshToken {
        @Test
        void shouldRefreshTokenSuccessfully() {
            Jwt jwt = mock(Jwt.class);
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenReturn(jwt);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(refreshTokenRepository.findByToken(refreshTokenRequestDto.refreshToken())).thenReturn(Optional.of(oldRefreshToken));


            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("asafe");
            when(userDetails.getAuthorities()).thenReturn(List.of());
            when(customUserDetailsService.loadUserByUsername(user.getUsername())).thenReturn(userDetails);


            when(tokenService.getAccessToken(any())).thenReturn("new-access-accessToken");
            when(tokenService.getRefreshToken(any())).thenReturn("new-refresh-accessToken");
            when(tokenService.getAccessTokenExpiration()).thenReturn(300L);
            when(tokenService.getRefreshTokenExpiration()).thenReturn(28800L);

            LoginResponseDto loginResponseDto = authService.refreshToken(refreshTokenRequestDto);

            assertNotNull(loginResponseDto);
            assertEquals("new-refresh-accessToken", loginResponseDto.refreshToken());
            assertEquals("new-access-accessToken", loginResponseDto.accessToken());

            assertTrue(oldRefreshToken.isRevoked());
            verify(refreshTokenRepository, times(2)).save(any());

        }

        @Test
        void shouldThrowExceptionWhenTokenTypeIsNotRefresh() {
            String token = "invalid-accessToken";

            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaim("type")).thenReturn("access");
            when(jwtDecoder.decode(token)).thenReturn(jwt);

            assertThrows(UnauthorizedException.class, () -> authService.refreshToken(new RefreshTokenRequestDto(token)));

            verify(refreshTokenRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenRefreshTokenNotFound() {
            String token = "non-existent-accessToken";

            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(jwtDecoder.decode(token)).thenReturn(jwt);

            when(refreshTokenRepository.findByToken(token)).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> authService.refreshToken(new RefreshTokenRequestDto(token)));
        }

        @Test
        void shouldThrowExceptionWhenRefreshTokenIsRevoked() {
            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenReturn(jwt);

            oldRefreshToken.setRevoked(true);

            when(refreshTokenRepository.findByToken(oldRefreshToken.getToken())).thenReturn(Optional.of(oldRefreshToken));

            assertThrows(UnauthorizedException.class, () -> authService.refreshToken(new RefreshTokenRequestDto(refreshTokenRequestDto.refreshToken())));
        }
    }

    @Nested
    class logout {
        @Test
        void shouldLogoutSuccessfully(){
            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenReturn(jwt);
            when(refreshTokenRepository.findByToken(refreshTokenRequestDto.refreshToken())).thenReturn(Optional.of(oldRefreshToken));

            authService.logout(refreshTokenRequestDto);
            verify(refreshTokenRepository, times(1)).save(refreshTokenArgumentCaptor.capture());
            RefreshToken refreshTokenCaptured = refreshTokenArgumentCaptor.getValue();

            assertTrue(refreshTokenCaptured.isRevoked());
        }

        @Test
        void shouldThrowExceptionWhenJwtIsInvalidOrExpired() {
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenThrow(new JwtException("Invalid"));

            assertThrows(UnauthorizedException.class, () -> authService.logout(refreshTokenRequestDto));

            verify(refreshTokenRepository, never()).findByToken(any());
            verify(refreshTokenRepository, never()).save(any());
        }

        @Test
        void shouldThrowExceptionWhenTokenTypeIsNotRefresh() {
            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaim("type")).thenReturn("access");
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenReturn(jwt);

            assertThrows(UnauthorizedException.class, () -> authService.logout(refreshTokenRequestDto));

            verify(refreshTokenRepository, never()).findByToken(any());
        }
        @Test
        void shouldThrowExceptionWhenRefreshTokenNotFound() {
            Jwt jwt = mock(Jwt.class);
            when(jwt.getClaim("type")).thenReturn("refresh");
            when(jwtDecoder.decode(refreshTokenRequestDto.refreshToken())).thenReturn(jwt);

            when(refreshTokenRepository.findByToken(refreshTokenRequestDto.refreshToken())).thenReturn(Optional.empty());

            assertThrows(ResourceNotFoundException.class, () -> authService.logout(refreshTokenRequestDto));
        }
    }

}