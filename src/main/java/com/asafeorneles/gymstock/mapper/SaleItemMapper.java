package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.SaleItem.ResponseSaleItemDto;
import com.asafeorneles.gymstock.entities.SaleItem;

import java.util.ArrayList;
import java.util.List;

public class SaleItemMapper {
    public static List<ResponseSaleItemDto> saleItemsToResponseSaleItems(List<SaleItem> saleItems) {
        List<ResponseSaleItemDto> responseSaleItemsDto = new ArrayList<>();
        for (SaleItem saleItem : saleItems) {
            var responseSaleItemDto = new ResponseSaleItemDto(
                    saleItem.getSaleItemId(),
                    saleItem.getProduct().getProductId(),
                    saleItem.getProduct().getName(),
                    saleItem.getQuantity(),
                    saleItem.getUnityPrice(),
                    saleItem.getTotalPrice()
            );
            responseSaleItemsDto.add(responseSaleItemDto);
        }
        return responseSaleItemsDto;
    }
}
