package com.asafeorneles.gymstock.dtos.analytics;

import java.math.BigDecimal;

public record TopSellingProductsDto(
        String productId,
        String productName,
        BigDecimal quantitySold
) {
}
