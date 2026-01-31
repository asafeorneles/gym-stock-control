package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.coupon.CouponAppliedDto;
import com.asafeorneles.gymstock.dtos.sale.CreateSaleDto;
import com.asafeorneles.gymstock.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gymstock.dtos.user.SoldByUserDto;
import com.asafeorneles.gymstock.entities.Sale;
import com.asafeorneles.gymstock.entities.User;

public class SaleMapper {
    public static Sale createSaleToSale(CreateSaleDto createSaleDto, User user) {
        return Sale.builder()
                .paymentMethod(createSaleDto.paymentMethod())
                .user(user)
                .build();
    }

    public static ResponseSaleDto saleToResponseSale(Sale sale) {
        if (sale.containsCoupon()) {
            return new ResponseSaleDto(
                    sale.getSaleId(),
                    SaleItemMapper.saleItemsToResponseSaleItems(sale.getSaleItems()),
                    new CouponAppliedDto(sale.getCoupon().getCode(), sale.getDiscountAmount()),
                    sale.getTotalPrice(),
                    sale.getPaymentMethod(),
                    new SoldByUserDto(sale.getUser().getUsername(), sale.getUser().getUserId()),
                    sale.getCreatedDate()
            );
        }

        return new ResponseSaleDto(
                sale.getSaleId(),
                SaleItemMapper.saleItemsToResponseSaleItems(sale.getSaleItems()),
                null,
                sale.getTotalPrice(),
                sale.getPaymentMethod(),
                new SoldByUserDto(sale.getUser().getUsername(), sale.getUser().getUserId()),
                sale.getCreatedDate()
        );
    }
}

