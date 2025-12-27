package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.coupon.CreateCouponDto;
import com.asafeorneles.gym_stock_control.dtos.coupon.ResponseCouponDto;
import com.asafeorneles.gym_stock_control.services.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coupon")
public class CouponController {
    @Autowired
    CouponService couponService;

    @PostMapping
    public ResponseEntity<ResponseCouponDto> createCoupon(@RequestBody @Valid CreateCouponDto createCouponDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(createCouponDto));
    }

    @GetMapping
    public ResponseEntity<List<ResponseCouponDto>> getAllCoupons(){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseCouponDto> getCouponById(@PathVariable(value = "id") UUID id){
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.getCouponById(id));
    }

}
