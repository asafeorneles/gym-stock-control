package com.asafeorneles.gym_stock_control.entities;

import com.asafeorneles.gym_stock_control.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_sales")
@Getter
@Setter
@NoArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sale_id")
    private UUID saleId;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> saleItems;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "created_date")
    private LocalDateTime created_date;

    @PrePersist
    public void prePersist() {
        this.created_date = LocalDateTime.now();
    }

    public Sale(List<SaleItem> saleItems, BigDecimal totalPrice, PaymentMethod paymentMethod) {
        this.saleItems = saleItems;
        this.totalPrice = totalPrice;
        this.paymentMethod = paymentMethod;
    }
}
