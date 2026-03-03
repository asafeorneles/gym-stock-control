package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.coupon.CouponAppliedResponse;
import com.asafeorneles.gymstock.dtos.sale.SaleCreateRequest;
import com.asafeorneles.gymstock.dtos.sale.SaleResponse;
import com.asafeorneles.gymstock.entities.Sale;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, SaleItemMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR

)
public interface SaleMapper {

    @Mapping(target = "saleId", ignore = true)
    @Mapping(target = "discountAmount", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "coupon", ignore = true)
    @Mapping(target = "saleItems", ignore = true)
    Sale toEntity(SaleCreateRequest saleCreateRequest);

    @Mapping(target = "soldBy", source = "user")
    @Mapping(target = "couponApplied", expression = "java(mapCoupon(sale))")
    SaleResponse toResponse(Sale sale);

    default CouponAppliedResponse mapCoupon(Sale sale) {
        if (sale.getCoupon() == null) {
            return null;
        }

        return new CouponAppliedResponse(
                sale.getCoupon().getCode(),
                sale.getDiscountAmount()
        );
    }
}
