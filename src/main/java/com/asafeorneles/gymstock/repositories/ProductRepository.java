package com.asafeorneles.gymstock.repositories;

import com.asafeorneles.gymstock.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    boolean existsByNameAndBrand(String name, String brand);
    boolean existsByCategory_CategoryId(UUID id);

    @Query(value =
            """
                    SELECT p FROM Product p
                    WHERE p.inventory.quantity <= p.inventory.lowStockThreshold
                    """)
    List<Product> findProductWithLowStock();
}
