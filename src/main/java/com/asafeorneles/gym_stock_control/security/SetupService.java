package com.asafeorneles.gym_stock_control.security;

import com.asafeorneles.gym_stock_control.dtos.auth.FirstAdminDto;
import com.asafeorneles.gym_stock_control.entities.Role;
import com.asafeorneles.gym_stock_control.entities.User;
import com.asafeorneles.gym_stock_control.enums.RoleName;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.repositories.RoleRepository;
import com.asafeorneles.gym_stock_control.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class SetupService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Transactional
    public void createFirstAdmin(FirstAdminDto firstAdminDto) {

        if (userRepository.existsByRoles_Name(RoleName.ROLE_ADMIN.name())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um administrador");
        }

        Role roleAdmin = roleRepository.findByName(RoleName.ROLE_ADMIN.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User firstAdmin = User.builder()
                .username(firstAdminDto.username())
                .password(passwordEncoder.encode(firstAdminDto.password()))
                .roles(Set.of(roleAdmin))
                .build();

        userRepository.save(firstAdmin);
    }
}
