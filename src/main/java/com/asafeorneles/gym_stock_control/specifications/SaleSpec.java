package com.asafeorneles.gym_stock_control.specifications;

import com.asafeorneles.gym_stock_control.entities.Sale;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleSpec {
    public static Specification<Sale> totalPriceGreaterThanOrEqualTo(BigDecimal totalPrice){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(totalPrice)){
                return null;
            }

            return builder.greaterThanOrEqualTo(root.get("totalPrice"), totalPrice);
        };
    }

    public static Specification<Sale> totalPriceLessThanOrEqualTo(BigDecimal totalPrice){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(totalPrice)){
                return null;
            }

            return builder.lessThanOrEqualTo(root.get("totalPrice"), totalPrice);
        };
    }

    public static Specification<Sale> createdAtGreaterThanOrEqualTo(LocalDateTime createdDate){
        return (root, query, builder) -> {
            if (createdDate == null){
                return null;
            }

            return builder.greaterThanOrEqualTo(root.get("createdDate"), createdDate);
        };
    }

    public static Specification<Sale> createdAtLessThanOrEqualTo(LocalDateTime createdDate){
        return (root, query, builder) -> {
            if (createdDate == null){
                return null;
            }

            return builder.lessThanOrEqualTo(root.get("createdDate"), createdDate);
        };
    }
}
