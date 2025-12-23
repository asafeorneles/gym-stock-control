package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.product.CreateProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDetailDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.UpdateProductDto;
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
            @ApiResponse(responseCode = "409", description = "Product with same name and brand already exists"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductDetailDto> createProduct(@RequestBody @Valid CreateProductDto createProductDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @Operation(summary = "Get all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product returned successfully"),
            @ApiResponse(responseCode = "404", description = "Products not found"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping
    public ResponseEntity<List<ResponseProductDto>> findProducts(@ParameterObject ProductQueryFilters filters) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProducts(filters.toSpecification()));
    }

    @Operation(summary = "Get all products with details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product returned successfully"),
            @ApiResponse(responseCode = "404", description = "Products not found"),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/details")
    public ResponseEntity<List<ResponseProductDetailDto>> findProductsDetails(@ParameterObject ProductDetailsQueryFilters filters) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProductsDetails(filters.toSpecification()));
    }

    @Operation(summary = "Get a product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<ResponseProductDto> findProductById(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProductById(id));
    }

    @Operation(summary = "Get all products with low stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product returned successfully"),
            @ApiResponse(responseCode = "404", description = "Products not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/low-stock")
    public ResponseEntity<List<ResponseProductDetailDto>> findProductsWithLowStock (){
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProductsWithLowStock());
    }

    @Operation(summary = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating product"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductDetailDto> updateProduct(@PathVariable(name = "id") UUID id, @RequestBody @Valid UpdateProductDto updateProductDto){
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, updateProductDto));
    }

    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable(name = "id") UUID id){
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Pet deleted successfully");
    }
}
