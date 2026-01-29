package com.asafeorneles.gymstock.specifications;

import com.asafeorneles.gymstock.entities.Coupon;
import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.enums.DiscountType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

public class CouponSpec {

    public static Specification<Coupon> codeContains(String code){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(code)){
                return null;
            }
            return builder.like(root.get("code"), "%" + code + "%");
        };
    }

    public static Specification<Coupon> activityStatusEquals(ActivityStatus activityStatus){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(activityStatus)){
                return null;
            }
            return builder.equal(root.get("activityStatus"), activityStatus);
        };
    }

    public static Specification<Coupon> discountTypeEquals(DiscountType discountType){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(discountType)){
                return null;
            }
            return builder.equal(root.get("discountType"), discountType);
        };
    }
}
