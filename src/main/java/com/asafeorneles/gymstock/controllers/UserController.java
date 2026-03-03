package com.asafeorneles.gymstock.controllers;

import com.asafeorneles.gymstock.dtos.user.UserResponse;
import com.asafeorneles.gymstock.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@Tag(name = "Users")
@RequiredArgsConstructor
public class UserController {

    final UserService userService;

    @Operation(summary = "Get all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users returned successfully"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers(){
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @Operation(summary = "Get a user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users returned successfully"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('user:read')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable(name = "id") UUID id){
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @Operation(summary = "Deactivate a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User is already inactive"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('user:deactivate')")
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateUser(@PathVariable(name = "id") UUID id, JwtAuthenticationToken token){
        userService.deactivateUser(id, token);
        return ResponseEntity.status(HttpStatus.OK).body("User deactivated successfully");
    }

    @Operation(summary = "Activate a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User activated successfully"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "User is already active"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('user:activate')")
    @PatchMapping("/activate/{id}")
    public ResponseEntity<String> activateUser(@PathVariable(name = "id") UUID id){
        userService.activityUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("User activate successfully");
    }
}
