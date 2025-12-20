package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.sale.CreateSaleDto;
import com.asafeorneles.gym_stock_control.dtos.sale.PatchPaymentMethodDto;
import com.asafeorneles.gym_stock_control.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gym_stock_control.queryFilters.SaleQueryFilters;
import com.asafeorneles.gym_stock_control.services.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/sales", produces = {"application/json"})
@Tag(name = "Sales")
public class SaleController {

    @Autowired
    SaleService saleService;

    @Operation(summary = "Create a Sale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sale created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Insufficient product quantity in stock"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping
    public ResponseEntity<ResponseSaleDto> createSale(@RequestBody @Valid CreateSaleDto createSaleDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.createSale(createSaleDto));
    }

    @Operation(summary = "Get all sales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales returned successfully"),
            @ApiResponse(responseCode = "404", description = "Sales not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping
    public ResponseEntity<List<ResponseSaleDto>> findSales(@ParameterObject SaleQueryFilters filters){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.findSales(filters.toSpecification()));
    }

    @Operation(summary = "Get a sale by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "404", description = "Sale not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseSaleDto> findSales(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.findSaleById(id));
    }

    @Operation(summary = "Update the payment method of a sale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sale updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Sale not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating sale"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PatchMapping("/payment-method/{id}")
    public ResponseEntity<ResponseSaleDto> updatePaymentMethod(@PathVariable(name = "id") UUID id, PatchPaymentMethodDto patchPaymentMethod){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.updatePaymentMethod(id, patchPaymentMethod));
    }

    @Operation(summary = "Delete a sale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "404", description = "Sale not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSale(@PathVariable(name = "id") UUID id){
        saleService.deleteSale(id);
        return ResponseEntity.status(HttpStatus.OK).body("Sale deleted successfully");
    }

}
