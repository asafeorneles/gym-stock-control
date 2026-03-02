package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.user.SoldByUserDto;
import com.asafeorneles.gymstock.dtos.user.UserResponseDto;
import com.asafeorneles.gymstock.entities.Role;
import com.asafeorneles.gymstock.entities.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserResponseDto toResponse(User user);

    SoldByUserDto soldByUser(User user);

    default String map(Role role){
        return role.getName();
    }
}
