package com.asafeorneles.gymstock.queryFilters;

import com.asafeorneles.gymstock.entities.Coupon;
import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.enums.DiscountType;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import static com.asafeorneles.gymstock.specifications.CouponSpec.*;

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
