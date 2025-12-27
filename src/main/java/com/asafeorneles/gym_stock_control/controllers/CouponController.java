package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.coupon.CreateCouponDto;
import com.asafeorneles.gym_stock_control.dtos.coupon.ResponseCouponDto;
import com.asafeorneles.gym_stock_control.services.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/coupon", produces = {"application/json"})
public class CouponController {
    @Autowired
    CouponService couponService;

    @Operation(summary = "Create a coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Coupon with same code and brand already exists"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseCouponDto> createCoupon(@RequestBody @Valid CreateCouponDto createCouponDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(createCouponDto));
    }

    @Operation(summary = "Get all coupons")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupons returned successfully"),
            @ApiResponse(responseCode = "404", description = "Coupons not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping
    public ResponseEntity<List<ResponseCouponDto>> getAllCoupons(){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.getAllCoupons());
    }

    @Operation(summary = "Get a coupon by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Coupon returned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ResponseCouponDto> getCouponById(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.getCouponById(id));
    }

    @Operation(summary = "Deactivate a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon deactivated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "409", description = "Coupon is already inactive"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ResponseCouponDto> deactivateCoupon(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(couponService.deactivateCoupon(id));
    }

    @Operation(summary = "Activate a coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Coupon activated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Coupon not found"),
            @ApiResponse(responseCode = "409", description = "Coupon is already active"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @PatchMapping("/{id}/activity")
    public ResponseEntity<ResponseCouponDto> activityCoupon(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(couponService.activityCoupon(id));
    }

    @Operation(summary = "Delete a coupon")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "coupon deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid ID format"),
            @ApiResponse(responseCode = "404", description = "coupon not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @DeleteMapping ("/{id}")
    public ResponseEntity<String> deleteCoupon(@PathVariable(value = "id") UUID id){
        couponService.deleteCoupon(id);
        return ResponseEntity.status(HttpStatus.CREATED).body("Coupon deleted successfully");
    }
}
