package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.sale.CreateSaleDto;
import com.asafeorneles.gym_stock_control.dtos.sale.PatchPaymentMethodDto;
import com.asafeorneles.gym_stock_control.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gym_stock_control.services.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/{id}")
    public ResponseEntity<ResponseSaleDto> findSales(@PathVariable(name = "id") UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.findSaleById(id));
    }

    @PatchMapping("/payment-method/{id}")
    public ResponseEntity<ResponseSaleDto> updatePaymentMethod(@PathVariable(name = "id") UUID id, PatchPaymentMethodDto patchPaymentMethod){
        return ResponseEntity.status(HttpStatus.OK).body(saleService.updatePaymentMethod(id, patchPaymentMethod));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSale(@PathVariable(name = "id") UUID id){
        saleService.deleteSale(id);
        return ResponseEntity.status(HttpStatus.OK).body("Sale deleted successfully");
    }

}
