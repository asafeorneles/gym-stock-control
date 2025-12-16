package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryLowStockThresholdDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryQuantityDto;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventoryDto;
import com.asafeorneles.gym_stock_control.services.ProductInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/inventory", produces = {"application/json"})
@Tag(name = "Products Inventories")
public class InventoryController {
    @Autowired
    ProductInventoryService productInventoryService;

    @Operation(summary = "Get all products inventories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product inventories returned successfully"),
            @ApiResponse(responseCode = "404", description = "Product inventories not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping
    public ResponseEntity<List<ResponseProductInventoryDto>> findProductsInventories(){
        return ResponseEntity.status(HttpStatus.OK).body(productInventoryService.findProductsInventories());
    }

    @Operation(summary = "Update the quantity of a product inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product inventory quantity updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Product inventory not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating inventory"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PatchMapping(value = "/quantity/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductInventoryDto> updateQuantity(@PathVariable(name = "id") UUID id, PatchProductInventoryQuantityDto patchProductInventoryQuantity){
        return ResponseEntity.status(HttpStatus.OK).body(productInventoryService.updateQuantity(id, patchProductInventoryQuantity));
    }

    @Operation(summary = "Update the low stock threshold of a product inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Low stock threshold updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Product inventory not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating inventory"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PatchMapping(value = "/low-stock/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseProductInventoryDto> updateQuantity(@PathVariable(name = "id") UUID id, PatchProductInventoryLowStockThresholdDto patchProductInventoryLowStockThreshold){
        return ResponseEntity.status(HttpStatus.OK).body(productInventoryService.updateLowStockThreshold(id, patchProductInventoryLowStockThreshold));
    }
}
