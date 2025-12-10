package com.asafeorneles.gym_stock_control.controllers;

import com.asafeorneles.gym_stock_control.dtos.ProductInventory.PatchProductInventoryQuantity;
import com.asafeorneles.gym_stock_control.dtos.ProductInventory.ResponseProductInventory;
import com.asafeorneles.gym_stock_control.services.ProductInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    @Autowired
    ProductInventoryService productInventoryService;

    @GetMapping()
    public ResponseEntity<List<ResponseProductInventory>> findProductsInventories(){
        return ResponseEntity.status(HttpStatus.OK).body(productInventoryService.findProducts());
    }
}
