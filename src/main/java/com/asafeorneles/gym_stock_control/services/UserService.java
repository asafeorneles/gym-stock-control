package com.asafeorneles.gym_stock_control.services;

import com.asafeorneles.gym_stock_control.dtos.user.UserResponseDto;
import com.asafeorneles.gym_stock_control.exceptions.ResourceNotFoundException;
import com.asafeorneles.gym_stock_control.mapper.UserMapper;
import com.asafeorneles.gym_stock_control.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
