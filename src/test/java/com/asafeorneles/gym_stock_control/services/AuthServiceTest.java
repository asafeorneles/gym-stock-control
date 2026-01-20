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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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

    @InjectMocks
    AuthService authService;

    private LoginRequestDto loginRequestDto;
    private RegisterRequestDto registerRequestDto;
    private Role role;

    @BeforeEach
    void setUp() {
        loginRequestDto = new LoginRequestDto("zafin", "123");
        registerRequestDto = new RegisterRequestDto("zafin", "123", "ROLE_BASIC");
        role = new Role(2L, "BASIC");
    }

    @Test
    void shouldLoginSuccessful() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateToken(authentication)).thenReturn("token-teste");
        when(tokenService.getTokenExpiresIn()).thenReturn(300L);

        LoginResponseDto loginResponseDto = authService.login(loginRequestDto);


        assertNotNull(loginResponseDto);
        assertEquals("token-teste", loginResponseDto.token());
        assertEquals(300L, loginResponseDto.expiresIn());
    }

    @Nested
    class register {
        @Test
        void shouldRegisterAUserSuccessfully(){
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
}