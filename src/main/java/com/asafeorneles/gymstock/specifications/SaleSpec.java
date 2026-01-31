package com.asafeorneles.gymstock.specifications;

import com.asafeorneles.gymstock.entities.Sale;
import com.asafeorneles.gymstock.enums.PaymentMethod;
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

    public static Specification<Sale> paymentMethodEqual(PaymentMethod paymentMethod){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(paymentMethod)){
                return null;
            }
            return builder.equal(root.get("paymentMethod"), paymentMethod);
        };
    }
}
