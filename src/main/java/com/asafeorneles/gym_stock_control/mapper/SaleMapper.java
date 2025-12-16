package com.asafeorneles.gym_stock_control.mapper;

import com.asafeorneles.gym_stock_control.dtos.sale.CreateSaleDto;
import com.asafeorneles.gym_stock_control.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gym_stock_control.entities.Sale;

public class SaleMapper {
    public static Sale createSaleToSale(CreateSaleDto createSaleDto) {
        return Sale.builder()
                .paymentMethod(createSaleDto.paymentMethod())
                .build();
    }

    public static ResponseSaleDto saleToResponseSale(Sale sale) {
        return new ResponseSaleDto(
                sale.getSaleId(),
                SaleItemMapper.saleItemsToResponseSaleItems(sale.getSaleItems()),
                sale.getTotalPrice(),
                sale.getPaymentMethod(),
                sale.getCreatedDate()
        );
    }
}
