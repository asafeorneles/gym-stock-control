package com.asafeorneles.gymstock.specifications;

import com.asafeorneles.gymstock.entities.Product;
import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.enums.InventoryStatus;
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

    public static Specification<Product> brandContains(String brand) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(brand)) {
                return null;
            }
            return builder.like(root.get("brand"), "%" + brand + "%");
        };
    }

    public static Specification<Product> priceEqual(BigDecimal price) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(price)) {
                return null;
            }
            return builder.equal(root.get("price"), price);
        };
    }

    public static Specification<Product> priceGreaterThanOrEqualTo(BigDecimal price) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(price)) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Product> priceLessThanOrEqualTo(BigDecimal price) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(price)) {
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("price"), price);
        };
    }

    public static Specification<Product> costPriceEqual(BigDecimal costPrice) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(costPrice)) {
                return null;
            }
            return builder.equal(root.get("costPrice"), costPrice);
        };
    }

    public static Specification<Product> costPriceGreaterThanOrEqualTo(BigDecimal costPrice) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(costPrice)) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("costPrice"), costPrice);
        };
    }

    public static Specification<Product> costPriceLessThanOrEqualTo(BigDecimal costPrice) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(costPrice)) {
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("costPrice"), costPrice);
        };
    }

    public static Specification<Product> categoryIdEqual(UUID categoryId) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(categoryId)) {
                return null;
            }
            return builder.equal(root.get("category").get("categoryId"), categoryId);
        };
    }

    public static Specification<Product> inventoryStatus(InventoryStatus inventoryStatus) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(inventoryStatus)) {
                return null;
            }
            return builder.equal(root.get("inventory").get("inventoryStatus"), inventoryStatus);
        };
    }

    public static Specification<Product> activityStatusEquals(ActivityStatus activityStatus) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(activityStatus)) {
                return null;
            }
            return builder.equal(root.get("activityStatus"), activityStatus);
        };
    }

    public static Specification<Product> isActivity() {
        return (root, query, builder) ->
                builder.equal(root.get("activityStatus"), ActivityStatus.ACTIVE);
    }


}

