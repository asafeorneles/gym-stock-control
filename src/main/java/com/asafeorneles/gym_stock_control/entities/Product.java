package com.asafeorneles.gym_stock_control.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "brand", "category_id"})
})
@Getter
@Setter
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "name")
    private String name;

    @Column(name = "brand")
    private String brand;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn // The primary key for Products will be the primary key for Product Inventory
    private ProductInventory inventory;

    @Column(name = "created_date")
    private LocalDateTime created_date;

    @Column(name = "updated_date")
    private LocalDateTime updated_date;

    @PrePersist
    public void prePersist() {
        this.created_date = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updated_date = LocalDateTime.now();
    }

    public Product(String name, String brand, BigDecimal price, BigDecimal costPrice, Category category, ProductInventory inventory) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.costPrice = costPrice;
        this.category = category;
        this.inventory = inventory;
    }
}
