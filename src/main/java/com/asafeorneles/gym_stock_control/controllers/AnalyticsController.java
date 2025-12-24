package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.analytics.TopSellingProductsDto;
import com.asafeorneles.gym_stock_control.services.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = "/analytics", produces = {"application/json"})
@Tag(name = "Analytics")
@Validated
public class AnalyticsController {

    @Autowired
    AnalyticsService analyticsService;

    @Operation(summary = "Get top n best-selling products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product data returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameter format"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/top-products-best-selling")
    public ResponseEntity<List<TopSellingProductsDto>> getTopSellingProducts(@RequestParam(required = false) @Min(1)@Max(30)Integer limit){
        return ResponseEntity.status(HttpStatus.OK).body(analyticsService.getTopSellingProducts(limit));
    }

    @Operation(summary = "Get top n best-selling products by period")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product data returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format or missing required parameters"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/top-products-best-selling-by-period")
    public ResponseEntity<List<TopSellingProductsDto>> getTopSellingProductsByPeriod(@RequestParam(required = false) @Min(1)@Max(30)Integer limit, @RequestParam @NotNull LocalDate startDate, @RequestParam @NotNull LocalDate endDate){
        return ResponseEntity.status(HttpStatus.OK).body(analyticsService.getTopSellingProductsByPeriod(limit, startDate, endDate));
    }
}
