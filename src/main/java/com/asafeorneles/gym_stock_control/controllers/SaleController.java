package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.sale.CreateSaleDto;
import com.asafeorneles.gym_stock_control.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gym_stock_control.services.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/sales", produces = {"application/json"})
public class SaleController {

    @Autowired
    SaleService saleService;

    @PostMapping
    public ResponseEntity<ResponseSaleDto> createSale(@RequestBody @Valid CreateSaleDto createSaleDto){
        return ResponseEntity.status(HttpStatus.CREATED).body(saleService.createSale(createSaleDto));
    }

    @GetMapping
    public ResponseEntity<List<ResponseSaleDto>> findSales(){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.findSales());
    }
}
