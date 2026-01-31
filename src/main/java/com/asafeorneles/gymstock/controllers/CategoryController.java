package com.asafeorneles.gymstock.controllers;

import com.asafeorneles.gymstock.dtos.category.CreateCategoryDto;
import com.asafeorneles.gymstock.dtos.category.ResponseCategoryDetailsDto;
import com.asafeorneles.gymstock.dtos.category.UpdateCategoryDto;
import com.asafeorneles.gymstock.queryFilters.CategoryQueryFilters;
import com.asafeorneles.gymstock.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/categories", produces = {"application/json"})
@Tag(name = "Categories")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @Operation(summary = "Create a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "409", description = "Conflict creating category"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('category:create')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCategoryDetailsDto> createCategory(@RequestBody @Valid CreateCategoryDto createCategoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(createCategoryDto));
    }

    @Operation(summary = "Get all categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Categories not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('category:read')")
    @GetMapping
    public ResponseEntity<List<ResponseCategoryDetailsDto>> getAllCategories(@ParameterObject CategoryQueryFilters filters) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories(filters.toSpecification()));
    }

    @Operation(summary = "Get a category by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('category:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseCategoryDetailsDto> getCategoryById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoryById(id));
    }

    @Operation(summary = "Update a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating Category"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('category:update')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCategoryDetailsDto> updateCategory(@PathVariable(name = "id") UUID id, @RequestBody @Valid UpdateCategoryDto updateCategoryDto) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.updateCategory(id, updateCategoryDto));
    }


    @Operation(summary = "Activate a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category activated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category is already active"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('category:activate')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ResponseCategoryDetailsDto> activateCategory(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.activateCategory(id));
    }

    @Operation(summary = "Deactivate a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deactivated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category is already inactive"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('category:deactivate')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResponseCategoryDetailsDto> deactivateCategory(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.deactivateCategory(id));
    }

    @Operation(summary = "Delete a category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "This category has already been used in a product"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('category:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable(name = "id") UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.OK).body("Category deleted successfully");
    }
}
