package com.asafeorneles.gym_stock_control.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_product_inventories")
@Getter
@Setter
@NoArgsConstructor
public class ProductInventory {
    @Id
    @Column(name = "product_id")
    private UUID productInventoryId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id") // The column name is defined here
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "low_stock_threshould")
    private int lowStockThreshold;

    @Column(name = "update_date")
    private LocalDateTime updated_date;

    @PreUpdate
    public void preUpdate() {
        this.updated_date = LocalDateTime.now();
    }

    @Builder
    public ProductInventory(
            UUID productInventoryId,
            Product product,
            int quantity,
            int lowStockThreshold) {

        this.productInventoryId = productInventoryId;
        this.product = product;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }
}
