package com.asafeorneles.gymstock.services;

import com.asafeorneles.gymstock.dtos.user.UserResponseDto;
import com.asafeorneles.gymstock.entities.User;
import com.asafeorneles.gymstock.exceptions.BusinessConflictException;
import com.asafeorneles.gymstock.exceptions.ResourceNotFoundException;
import com.asafeorneles.gymstock.mapper.UserMapper;
import com.asafeorneles.gymstock.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::userToUserResponse)
                .toList();
    }

    public UserResponseDto getUserById(UUID id) {
        return userRepository.findById(id)
                .map(UserMapper::userToUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by this id: " + id));
    }

    @Transactional
    public void deactivateUser(UUID id, JwtAuthenticationToken token) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by this id: " + id));

        UUID userId = UUID.fromString(token.getName());
        if (user.getUserId().equals(userId)){
            throw new BusinessConflictException("You cannot deactivate your own user account.");
        }

        user.inactivity();

        userRepository.save(user);
    }

    @Transactional
    public void activityUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found by this id: " + id));

        user.activity();

        userRepository.save(user);
    }
}
