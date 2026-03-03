package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.user.UserSoldByResponse;
import com.asafeorneles.gymstock.dtos.user.UserResponse;
import com.asafeorneles.gymstock.entities.Role;
import com.asafeorneles.gymstock.entities.User;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface UserMapper {
    UserResponse toResponse(User user);

    UserSoldByResponse soldByUser(User user);

    default String map(Role role){
        return role.getName();
    }
}
