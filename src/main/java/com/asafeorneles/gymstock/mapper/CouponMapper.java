package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.coupon.CreateCouponRequest;
import com.asafeorneles.gymstock.dtos.coupon.CouponResponse;
import com.asafeorneles.gymstock.entities.Coupon;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CouponMapper {
    @Mapping(target = "couponId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    Coupon toEntity(CreateCouponRequest createCouponRequest);

    CouponResponse toResponse(Coupon coupon);
}
