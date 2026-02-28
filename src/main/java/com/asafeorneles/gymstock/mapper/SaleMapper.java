package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.coupon.CouponAppliedDto;
import com.asafeorneles.gymstock.dtos.sale.CreateSaleDto;
import com.asafeorneles.gymstock.dtos.sale.ResponseSaleDto;
import com.asafeorneles.gymstock.entities.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, SaleItemMapper.class})
public interface SaleMapper {

    @Mapping(target = "saleId", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    @Mapping(target = "saleItems", ignore = true)
    Sale toEntity(CreateSaleDto createSaleDto);

    @Mapping(target = "soldBy", source = "user")
    @Mapping(target = "couponApplied", expression = "java(mapCoupon(sale))")
    ResponseSaleDto toResponse(Sale sale);

    default CouponAppliedDto mapCoupon(Sale sale) {
        if (sale.getCoupon() == null) {
            return null;
        }

        return new CouponAppliedDto(
                sale.getCoupon().getCode(),
                sale.getDiscountAmount()
        );
    }
}
