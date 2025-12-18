package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.analytics.TopSellingProductsDto;
import com.asafeorneles.gym_stock_control.services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    AnalyticsService analyticsService;

    @GetMapping("/top-products-must-sales")
    public ResponseEntity<List<TopSellingProductsDto>> getTopSellingProducts(@RequestParam(required = false) Integer limit){
        return ResponseEntity.status(HttpStatus.OK).body(analyticsService.getTopSellingProducts(limit));
    }

    @GetMapping("/top-products-must-sales-by-period")
    public ResponseEntity<List<TopSellingProductsDto>> getTopSellingProductsByPeriod(@RequestParam(required = false) Integer limit, LocalDate startDate, LocalDate endDate){
        return ResponseEntity.status(HttpStatus.OK).body(analyticsService.getTopSellingProductsByPeriod(limit, startDate, endDate));
    }
}
