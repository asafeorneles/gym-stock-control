package com.asafeorneles.gymstock.repositories;

import com.asafeorneles.gymstock.entities.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

    boolean existsByProduct_ProductId(UUID id);
}
