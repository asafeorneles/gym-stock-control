package com.asafeorneles.gym_stock_control.repositories;

import com.asafeorneles.gym_stock_control.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

    boolean existsByProduct_id(UUID id);
}
