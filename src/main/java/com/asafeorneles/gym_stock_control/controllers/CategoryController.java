package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.category.CreateCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.ResponseCategoryDto;
import com.asafeorneles.gym_stock_control.dtos.category.UpdateCategoryDto;
import com.asafeorneles.gym_stock_control.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/categories")
    public ResponseEntity<ResponseCategoryDto> createCategory(@RequestBody @Valid CreateCategoryDto createCategoryDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryDto));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ResponseCategoryDto>> findCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findCategory());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ResponseCategoryDto> findCategories(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findCategoryById(id));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ResponseCategoryDto> updateCategory(@PathVariable(name = "id") UUID id, @RequestBody @Valid UpdateCategoryDto updateCategoryDto){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategory(id, updateCategoryDto));
    }
}
