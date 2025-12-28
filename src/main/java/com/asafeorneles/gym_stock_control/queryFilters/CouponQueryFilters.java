package com.asafeorneles.gym_stock_control.queryFilters;

import com.asafeorneles.gym_stock_control.entities.Coupon;
import com.asafeorneles.gym_stock_control.enums.ActivityStatus;
import com.asafeorneles.gym_stock_control.enums.DiscountType;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import static com.asafeorneles.gym_stock_control.specifications.CouponSpec.*;

@Data
public class CouponQueryFilters {
    private String code;
    private ActivityStatus activityStatus;
    private DiscountType discountType;

    public Specification<Coupon> toSpecification() {

        return codeContains(code)
                .and(activityStatusEquals(activityStatus))
                .and(discountTypeEquals(discountType));
    }
}
