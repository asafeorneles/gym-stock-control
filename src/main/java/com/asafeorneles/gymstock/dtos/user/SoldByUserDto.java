package com.asafeorneles.gymstock.dtos.user;

import java.util.UUID;

public record SoldByUserDto (
        String username,
        UUID userId
){
}
