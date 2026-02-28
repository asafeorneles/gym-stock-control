package com.asafeorneles.gymstock.mapper;

import com.asafeorneles.gymstock.dtos.coupon.CreateCouponDto;
import com.asafeorneles.gymstock.dtos.coupon.ResponseCouponDto;
import com.asafeorneles.gymstock.entities.Coupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CouponMapper {
    @Mapping(target = "couponId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    Coupon toEntity(CreateCouponDto createCouponDto);

    ResponseCouponDto toResponse(Coupon coupon);
}
