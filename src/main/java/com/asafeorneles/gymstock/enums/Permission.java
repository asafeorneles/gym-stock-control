package com.asafeorneles.gymstock.enums;

import lombok.Getter;

@Getter
public enum Permission {
    USER_REGISTER("user:register"),
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_DELETE("user:delete"),
    USER_DEACTIVATE("user:deactivate"),
    USER_ACTIVATE("user:activate"),

    PRODUCT_CREATE("product:create"),
    PRODUCT_READ("product:read"),
    PRODUCT_DETAILS_READ("productDetails:read"),
    PRODUCT_LOW_STOCK_READ("productLowStock:read"),
    PRODUCT_UPDATE("product:update"),
    PRODUCT_DEACTIVATE("product:deactivate"),
    PRODUCT_ACTIVATE("product:activate"),
    PRODUCT_DELETE("product:delete"),

    CATEGORY_CREATE("category:create"),
    CATEGORY_READ("category:read"),
    CATEGORY_UPDATE("category:update"),
    CATEGORY_DEACTIVATE("category:deactivate"),
    CATEGORY_ACTIVATE("category:activate"),
    CATEGORY_DELETE("category:delete"),

    PRODUCT_INVENTORY_READ("inventory:read"),
    PRODUCT_INVENTORY_UPDATE_QUANTITY("inventory:updateQuantity"),
    PRODUCT_INVENTORY_UPDATE_LOW_STOCK("inventory:updateLowStock"),

    SALE_CREATE("sale:create"),
    SALE_READ("sale:read"),
    SALE_UPDATE("sale:update"),
    SALE_DELETE("sale:delete"),

    COUPON_CREATE("coupon:create"),
    COUPON_READ("coupon:read"),
    COUPON_DEACTIVATE("coupon:deactivate"),
    COUPON_ACTIVATE("coupon:activate"),
    COUPON_DELETE("coupon:delete"),

    ANALYTICS_READ("analytics:read");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

}
