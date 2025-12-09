package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.product.CreateProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.ResponseProductDto;
import com.asafeorneles.gym_stock_control.dtos.product.UpdateProductDto;
import com.asafeorneles.gym_stock_control.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ProductController {
    @Autowired
    ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<ResponseProductDto> createProduct(@RequestBody @Valid CreateProductDto createProductDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(createProductDto));
    }

    @GetMapping("/products")
    public ResponseEntity<List<ResponseProductDto>> findProducts() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ResponseProductDto> findProductById(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProductById(id));
    }

    @GetMapping("/products/low-stock")
    public ResponseEntity<List<ResponseProductDto>> findProductsWithLowStock (){
        return ResponseEntity.status(HttpStatus.OK).body(productService.findProductsWithLowStock());
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ResponseProductDto> updateProduct(@PathVariable(name = "id") UUID id, @RequestBody @Valid UpdateProductDto updateProductDto){
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, updateProductDto));
    }
}
