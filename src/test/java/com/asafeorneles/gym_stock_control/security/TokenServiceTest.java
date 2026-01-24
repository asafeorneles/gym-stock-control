package com.asafeorneles.gym_stock_control.security;

import com.asafeorneles.gym_stock_control.entities.User;
import com.asafeorneles.gym_stock_control.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    JwtEncoder jwtEncoder;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    TokenService tokenService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userId(UUID.randomUUID())
                .username("asafe")
                .build();

        ReflectionTestUtils.setField(tokenService, "accessTokenExpiration", 300L);
        ReflectionTestUtils.setField(tokenService, "refreshTokenExpiration", 28800L);
    }

    @Test
    void shouldGenerateAccessTokenSuccessfully() {
        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("asafe");
        doReturn(authorities).when(authentication).getAuthorities();

        when(userRepository.findByUsername("asafe"))
                .thenReturn(Optional.of(user));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("fake-jwt-token");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        String token = tokenService.getAccessToken(authentication);

        assertNotNull(token);
        assertEquals("fake-jwt-token", token);
    }

    @Test
    void shouldGenerateRefreshTokenSuccessfully() {
        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("asafe");
        doReturn(authorities).when(authentication).getAuthorities();

        when(userRepository.findByUsername("asafe"))
                .thenReturn(Optional.of(user));

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("fake-jwt-token");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        String token = tokenService.getRefreshToken(authentication);

        assertNotNull(token);
        assertEquals("fake-jwt-token", token);
    }

    @Test
    void shouldGetAccessTokenExpirationSuccessfully() {
        Long expires = tokenService.getAccessTokenExpiration();
        assertNotNull(expires);
    }
}