package com.asafeorneles.gym_stock_control.security;

import com.asafeorneles.gym_stock_control.dtos.auth.FirstAdminDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/setup")
public class SetupController {
    @Autowired
    SetupService setupService;

    @PostMapping("admin")
    public ResponseEntity<String> createFirstAdmin(@RequestBody FirstAdminDto firstAdminDto){
        setupService.createFirstAdmin(firstAdminDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("First admin created successfully");
    }
}
