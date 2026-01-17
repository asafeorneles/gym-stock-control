package com.asafeorneles.gym_stock_control.mapper;

import com.asafeorneles.gym_stock_control.dtos.user.UserResponseDto;
import com.asafeorneles.gym_stock_control.entities.Role;
import com.asafeorneles.gym_stock_control.entities.User;

import java.util.stream.Collectors;

public class UserMapper {

    public static UserResponseDto userToUserResponse(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }
}
