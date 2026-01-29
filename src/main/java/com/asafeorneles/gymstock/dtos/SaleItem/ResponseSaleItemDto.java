package com.asafeorneles.gymstock.dtos.SaleItem;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseSaleItemDto(
        UUID saleItemId,
        UUID productId,
        String nameProduct,
        int quantity,
        BigDecimal unityPrice,
        BigDecimal totalPrice
) {
}
