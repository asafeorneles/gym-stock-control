package com.asafeorneles.gym_stock_control.specifications;

import com.asafeorneles.gym_stock_control.entities.Product;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.UUID;

public class ProductSpec {
    public static Specification<Product> nameContains(String name) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(name)) {
                return null;
            }
            return builder.like(root.get("name"), "%" + name + "%");
        };
    }

    public static Specification<Product> brandContains(String brand){
        return (root, query, builder )->{
            if(ObjectUtils.isEmpty(brand)){
                return null;
            }
            return builder.like(root.get("brand"), "%" + brand + "%");
        };
    }

    public static Specification<Product> priceEqual(BigDecimal price){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(price)){
                return null;
            }
            return builder.equal(root.get("price"), price);
        };
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(BigDecimal price){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(price)){
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Product> priceLessThanOrEqualTo(BigDecimal price){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(price)){
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Product> costPriceEqual(BigDecimal costPrice){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(costPrice)){
                return null;
            }
            return builder.equal(root.get("costPrice"), costPrice);
        };
    }

    public static Specification<Product> costPriceGreaterThanOrEqualTo(BigDecimal costPrice){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(costPrice)){
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("costPrice"), costPrice);
        };
    }

    public static Specification<Product> costPriceLessThanOrEqualTo(BigDecimal costPrice){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(costPrice)){
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("costPrice"), costPrice);
        };
    }

    public static Specification<Product> categoryIdEqual(UUID categoryId){
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(categoryId)){
                return null;
            }
            return builder.equal(root.get("category").get("categoryId"), categoryId);
        };
    }

}
