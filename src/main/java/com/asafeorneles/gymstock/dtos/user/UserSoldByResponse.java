package com.asafeorneles.gymstock.dtos.user;

import java.util.UUID;

public record UserSoldByResponse(
        String username,
        UUID userId
){
}
