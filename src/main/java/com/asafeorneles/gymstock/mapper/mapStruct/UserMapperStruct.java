package com.asafeorneles.gymstock.mapper.mapStruct;

import com.asafeorneles.gymstock.dtos.user.UserResponseDto;
import com.asafeorneles.gymstock.entities.Role;
import com.asafeorneles.gymstock.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapperStruct {
    UserResponseDto toResponse(User user);

    default String map(Role role){
        return role.getName();
    }
}
