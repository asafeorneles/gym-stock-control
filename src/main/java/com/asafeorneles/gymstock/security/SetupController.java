package com.asafeorneles.gymstock.security;

import com.asafeorneles.gymstock.dtos.auth.FirstAdminDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setup")
@Tag(name = "Setup")
public class SetupController {
    @Autowired
    SetupService setupService;

    @Operation(summary = "Registers the first admin in the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "First admin registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "409", description = "There is already an admin in the system"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping("/first-admin")
    public ResponseEntity<String> createFirstAdmin(@RequestBody @Valid FirstAdminDto firstAdminDto){
        setupService.createFirstAdmin(firstAdminDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("First admin created successfully");
    }
}
