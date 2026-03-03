package com.asafeorneles.gymstock.entities;

import com.asafeorneles.gymstock.enums.InventoryStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_product_inventories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductInventory {
    @Id
    @Column(name = "product_id")
    private UUID productInventoryId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "low_stock_threshold")
    private int lowStockThreshold;

    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }
}
