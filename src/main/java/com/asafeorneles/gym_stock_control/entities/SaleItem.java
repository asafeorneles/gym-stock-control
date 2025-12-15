package com.asafeorneles.gym_stock_control.entities;

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
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "unity_price")
    private BigDecimal unityPrice;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;
}
