package com.asafeorneles.gymstock.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "tb_sale_itens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sale_item_id")
    private UUID saleItemId;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "cost_price", nullable = false)
    private BigDecimal costPrice;

    @Column(name = "unity_price", nullable = false)
    private BigDecimal unityPrice;

    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
}
