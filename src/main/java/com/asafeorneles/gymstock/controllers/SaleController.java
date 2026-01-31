package com.asafeorneles.gymstock.controllers;

import com.asafeorneles.gymstock.dtos.sale.CreateSaleDto;
import com.asafeorneles.gymstock.dtos.sale.PatchPaymentMethodDto;
import com.asafeorneles.gymstock.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gymstock.queryFilters.SaleQueryFilters;
import com.asafeorneles.gymstock.services.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

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
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "409", description = "Insufficient product quantity in stock"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('sale:create')")
    @PostMapping
    public ResponseEntity<ResponseSaleDto> createSale(@RequestBody @Valid CreateSaleDto createSaleDto, JwtAuthenticationToken token){
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.createSale(createSaleDto, token));
    }

    @Operation(summary = "Get all sales")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sales returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Sales not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('sale:read')")
    @GetMapping
    public ResponseEntity<Page<ResponseSaleDto>> getAllSales(@ParameterObject SaleQueryFilters filters, @PageableDefault(size = 5, sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.getAllSales(filters.toSpecification(), pageable));
    }

    @Operation(summary = "Get a sale by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Sale not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('sale:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseSaleDto> getSaleById(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.getSaleById(id));
    }

    @Operation(summary = "Update the payment method of a sale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sale updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Sale not found"),
            @ApiResponse(responseCode = "409", description = "Conflict updating sale"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('sale:update')")
    @PatchMapping("/payment-method/{id}")
    public ResponseEntity<ResponseSaleDto> updatePaymentMethod(@PathVariable(name = "id") UUID id, PatchPaymentMethodDto patchPaymentMethod){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.updatePaymentMethod(id, patchPaymentMethod));
    }

    @Operation(summary = "Delete a sale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Sale not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('sale:delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSale(@PathVariable(name = "id") UUID id){
        saleService.deleteSale(id);
        return ResponseEntity.status(HttpStatus.OK).body("Sale deleted successfully");
    }

}
