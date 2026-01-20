package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.coupon.CreateCouponDto;
import com.asafeorneles.gym_stock_control.dtos.coupon.ResponseCouponDto;
import com.asafeorneles.gym_stock_control.queryFilters.CouponQueryFilters;
import com.asafeorneles.gym_stock_control.services.CouponService;
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
@RequestMapping(value = "/coupon", produces = {"application/json"})
@Tag(name = "Coupons")
public class CouponController {
    @Autowired
    CouponService couponService;

    @Operation(summary = "Create a coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "409", description = "Coupon with same code and brand already exists"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('coupon:create')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCouponDto> createCoupon(@RequestBody @Valid CreateCouponDto createCouponDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(createCouponDto));
    }

    @Operation(summary = "Get all coupons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupons returned successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Coupons not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('coupon:read')")
    @GetMapping
    public ResponseEntity<List<ResponseCouponDto>> getAllCoupons(@ParameterObject CouponQueryFilters filters){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.getAllCoupons(filters.toSpecification()));
    }

    @Operation(summary = "Get a coupon by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('coupon:read')")
    @GetMapping("/{id}")
    public ResponseEntity<ResponseCouponDto> getCouponById(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.getCouponById(id));
    }

    @Operation(summary = "Deactivate a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "409", description = "Coupon is already inactive"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('coupon:deactivate')")
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResponseCouponDto> deactivateCoupon(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(couponService.deactivateCoupon(id));
    }

    @Operation(summary = "Activate a coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon activated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "409", description = "Coupon is already active"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('coupon:activate')")
    @PatchMapping("/{id}/activate")
    public ResponseEntity<ResponseCouponDto> activateCoupon(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(couponService.activateCoupon(id));
    }

    @Operation(summary = "Delete a coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated user"),
            @ApiResponse(responseCode = "403", description = "The logged-in user does not have permission to access this route."),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "409", description = "This coupon has already been used in a sale"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PreAuthorize("hasAuthority('coupon:delete')")
    @DeleteMapping ("/{id}")
    public ResponseEntity<String> deleteCoupon(@PathVariable(value = "id") UUID id){
        couponService.deleteCoupon(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Coupon deleted successfully");
    }
}
