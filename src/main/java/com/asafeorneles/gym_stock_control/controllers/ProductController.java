package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.product.*;
import com.asafeorneles.gym_stock_control.queryFilters.ProductDetailsQueryFilters;
import com.asafeorneles.gym_stock_control.queryFilters.ProductQueryFilters;
import com.asafeorneles.gym_stock_control.services.ProductService;
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
@RequestMapping(value = "/products", produces = {"application/json"})
@Tag(name = "Products")
public class ProductController {
    @Autowired
    ProductService productService;

    @Operation(summary = "Create a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "409", description = "Product with same name and brand already exists"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('product:create')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductDetailDto> createProduct(@RequestBody @Valid CreateProductDto createProductDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @Operation(summary = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Products not found"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('product:read')")
    @GetMapping
    public ResponseEntity<List<ResponseProductDto>> getAllProducts(@ParameterObject ProductQueryFilters filters) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts(filters.toSpecification()));
    }

    @Operation(summary = "Get all products with all details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Products not found"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('productDetails:read')")
    @GetMapping("/details")
    public ResponseEntity<List<ResponseProductDetailDto>> getAllProductsDetails(@ParameterObject ProductDetailsQueryFilters filters) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProductsDetails(filters.toSpecification()));
    }

    @Operation(summary = "Get a product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('product:read')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseProductDto> getProductById(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductById(id));
    }

    @Operation(summary = "Get all products with low stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Products not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('productLowStock:read')")
    @GetMapping("/low-stock")
    public ResponseEntity<List<ResponseProductDetailDto>> getAllProductsWithLowStock(){
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProductsWithLowStock());
    }

    @Operation(summary = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating product"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('product:update')")
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductDetailDto> updateProduct(@PathVariable(name = "id") UUID id, @RequestBody @Valid UpdateProductDto updateProductDto){
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, updateProductDto));
    }

    @Operation(summary = "Deactivate a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Product is already inactive"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('product:deactivate')")
    @PatchMapping(value = "/{id}/deactivate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductDetailDto> deactivateProduct(@PathVariable(name = "id") UUID id, @RequestBody @Valid DeactivateProductDto deactivateProductDto){
        return ResponseEntity.status(HttpStatus.OK).body(productService.deactivateProduct(id, deactivateProductDto));
    }

    @Operation(summary = "Activate a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product activated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Product is already active"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('product:activate')")
    @PatchMapping(value = "/{id}/activate")
    public ResponseEntity<ResponseProductDetailDto> activateProduct(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(productService.activateProduct(id));
    }

    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "This product has already been used in a sale"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('product:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "id") UUID id){
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Pet deleted successfully");
    }
}
