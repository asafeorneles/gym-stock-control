package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.user.UserResponseDto;
import com.asafeorneles.gymstock.entities.Role;
import com.asafeorneles.gymstock.entities.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponseDto userToUserResponse(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                user.getActivityStatus().name()
        );
    }
}
