package com.asafeorneles.gymstock.controllers;

import com.asafeorneles.gymstock.dtos.ProductInventory.PatchProductInventoryLowStockThresholdDto;
import com.asafeorneles.gymstock.dtos.ProductInventory.PatchProductInventoryQuantityDto;
import com.asafeorneles.gymstock.dtos.ProductInventory.ResponseProductInventoryDetailDto;
import com.asafeorneles.gymstock.services.ProductInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/inventories", produces = {"application/json"})
@Tag(name = "Products Inventories")
public class InventoryController {
    @Autowired
    ProductInventoryService productInventoryService;

    @Operation(summary = "Get all products inventories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product inventories returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product inventories not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('inventory:read')")
    @GetMapping
    public ResponseEntity<List<ResponseProductInventoryDetailDto>> findProductsInventories(){
        return ResponseEntity.status(HttpStatus.OK).body(productInventoryService.findProductsInventories());
    }

    @Operation(summary = "Get a products by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product inventories returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product inventory not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('inventory:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseProductInventoryDetailDto> findProductsInventories(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(productInventoryService.findProductInventoryById(id));
    }

    @Operation(summary = "Update the quantity of a product inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product inventory quantity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product inventory not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating inventory"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('inventory:updateQuantity')")
    @PatchMapping(value = "/quantity/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductInventoryDetailDto> updateQuantity(@PathVariable(name = "id") UUID id, @RequestBody PatchProductInventoryQuantityDto patchProductInventoryQuantity){
        return ResponseEntity.status(HttpStatus.OK).body(productInventoryService.updateQuantity(id, patchProductInventoryQuantity));
    }

    @Operation(summary = "Update the low stock threshold of a product inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Low stock threshold updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Product inventory not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating inventory"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('inventory:updateLowStock')")
    @PatchMapping(value = "/low-stock/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductInventoryDetailDto> updateQuantity(@PathVariable(name = "id") UUID id, @RequestBody PatchProductInventoryLowStockThresholdDto patchProductInventoryLowStockThreshold){
        return ResponseEntity.status(HttpStatus.OK).body(productInventoryService.updateLowStockThreshold(id, patchProductInventoryLowStockThreshold));
    }
}
