package com.asafeorneles.gymstock.entities;

import com.asafeorneles.gymstock.enums.ActivityStatus;
import com.asafeorneles.gymstock.enums.DiscountType;
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
@Table(name = "tb_coupon", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"code"})
})
@Getter
@Setter
@NoArgsConstructor
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "coupon_id")
    private UUID couponId;

    @Column(name = "code")
    private String code;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_value")
    private BigDecimal discountValue;

    @Column(name = "discount_type")
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "unlimited")
    private boolean unlimited;

    @Column(name = "activity_status")
    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

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

    public void inactivity(){
        if (this.activityStatus == ActivityStatus.INACTIVITY){
            throw new ActivityStatusException("Coupon is already inactive!");
        }

        this.activityStatus = ActivityStatus.INACTIVITY;
    }

    public void activity(){
        if (this.activityStatus == ActivityStatus.ACTIVE){
            throw new ActivityStatusException("Coupon is already active!");
        }
        this.activityStatus = ActivityStatus.ACTIVE;
    }

    public boolean isActivity(){
        return this.activityStatus == ActivityStatus.ACTIVE;
    }

    @Builder
    public Coupon(UUID couponId,
                  String code,
                  String description,
                  BigDecimal discountValue,
                  DiscountType discountType,
                  boolean unlimited,
                  int quantity,
                  ActivityStatus activityStatus,
                  LocalDateTime expirationDate) {

        this.couponId = couponId;
        this.code = code;
        this.description = description;
        this.discountValue = discountValue;
        this.discountType = discountType;
        this.unlimited = unlimited;
        this.quantity = quantity;
        this.activityStatus = activityStatus;
        this.expirationDate = expirationDate;
    }
}
