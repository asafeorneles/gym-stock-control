package com.asafeorneles.gymstock.entities;

import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.exceptions.ActivityStatusException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tb_products", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "brand"})
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

    @Column(name = "description")
    private String description;

    @Column(name = "sale_price")
    private BigDecimal price;

    @Column(name = "cost_price")
    private BigDecimal costPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private ProductInventory inventory;

    @Enumerated(EnumType.STRING)
    @Column(name = "activity_status", nullable = false)
    private ActivityStatus activityStatus;

    @Column(name = "inactivity_reason")
    private String inactivityReason;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = LocalDateTime.now();
    }

    public void inactivity(String inactivityReason){
        if (this.activityStatus == ActivityStatus.INACTIVITY){
            throw new ActivityStatusException("Product is already inactive!");
        }

        this.activityStatus = ActivityStatus.INACTIVITY;
        this.inactivityReason = inactivityReason;
    }

    public void activity(){
        if (this.activityStatus == ActivityStatus.ACTIVE){
            throw new ActivityStatusException("Product is already active!");
        }
        this.activityStatus = ActivityStatus.ACTIVE;
        this.inactivityReason = null;
    }

    public boolean isActivity(){
        return this.activityStatus == ActivityStatus.ACTIVE;
    }

    @Builder
    private Product(UUID productId,
                    String name,
                    String brand,
                    String description,
                    BigDecimal price,
                    BigDecimal costPrice,
                    Category category,
                    ProductInventory inventory) {

        this.productId = productId;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.price = price;
        this.costPrice = costPrice;
        this.category = category;
        this.inventory = inventory;
    }
}
