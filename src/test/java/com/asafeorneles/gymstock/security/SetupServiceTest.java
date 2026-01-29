package com.asafeorneles.gymstock.security;

import com.asafeorneles.gymstock.dtos.auth.FirstAdminDto;
import com.asafeorneles.gymstock.entities.Role;
import com.asafeorneles.gymstock.entities.User;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.repositories.RoleRepository;
import com.asafeorneles.gymstock.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class SetupServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    SetupService setupService;

    private Role role;
    private FirstAdminDto firstAdminDto;

    @BeforeEach
    void setUp() {
        role = new Role(1L, "ROLE_ADMIN");
        firstAdminDto = new FirstAdminDto("admin", "123");
    }

    @Nested
    class createFirstAdmin{
        @Test
        void shouldCreateFirstAdminSuccessfully() {
            when(userRepository.existsByRoles_Name("ROLE_ADMIN")).thenReturn(false);
            when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
            when(passwordEncoder.encode(firstAdminDto.password())).thenReturn("encoded-password");

            setupService.createFirstAdmin(firstAdminDto);

            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void shouldThrowExceptionWhenAdminAlreadyExists(){
            when(userRepository.existsByRoles_Name("ROLE_ADMIN")).thenReturn(true);

            assertThrows(BusinessConflictException.class, ()->  setupService.createFirstAdmin(firstAdminDto));
            verify(userRepository, never()).save(any(User.class));
        }
    }



}