package com.asafeorneles.gym_stock_control.queryFilters;

import com.asafeorneles.gym_stock_control.entities.Product;
import com.asafeorneles.gym_stock_control.enums.ActivityStatus;
import com.asafeorneles.gym_stock_control.enums.InventoryStatus;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

import static com.asafeorneles.gym_stock_control.specifications.ProductSpec.*;

@Data
public class ProductDetailsQueryFilters {
    private InventoryStatus inventoryStatus;
    private ActivityStatus activityStatus;
    private String name;
    private String brand;
    private BigDecimal price;
    private BigDecimal priceMax;
    private BigDecimal priceMin;
    private BigDecimal costPrice;
    private BigDecimal costPriceMax;
    private BigDecimal costPriceMin;
    private UUID categoryId;

    public Specification<Product> toSpecification() {
        return activityStatusEquals(activityStatus)
                .and(inventoryStatus(inventoryStatus))
                .and(nameContains(name))
                .and(brandContains(brand))
                .and(priceEqual(price))
                .and(priceGreaterThanOrEqualTo(priceMin))
                .and(priceLessThanOrEqualTo(priceMax))
                .and(costPriceEqual(costPrice))
                .and(costPriceGreaterThanOrEqualTo(costPriceMin))
                .and(costPriceLessThanOrEqualTo(costPriceMax))
                .and(categoryIdEqual(categoryId));
    }
}
