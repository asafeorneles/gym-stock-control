package com.asafeorneles.gymstock.security;

import com.asafeorneles.gymstock.dtos.auth.FirstAdminDto;
import com.asafeorneles.gymstock.entities.Role;
import com.asafeorneles.gymstock.entities.User;
import com.asafeorneles.gymstock.enums.RoleName;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.repositories.RoleRepository;
import com.asafeorneles.gymstock.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new BusinessConflictException("There is already an admin in the system.");
        }

        Role roleAdmin = roleRepository.findByName(RoleName.ROLE_ADMIN.name())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        User firstAdmin = User.builder()
                .username(firstAdminDto.username())
                .password(passwordEncoder.encode(firstAdminDto.password()))
                .roles(Set.of(roleAdmin))
                .build();

        firstAdmin.activity();

        userRepository.save(firstAdmin);
    }
}
