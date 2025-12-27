package com.asafeorneles.gym_stock_control.repositories;

import com.asafeorneles.gym_stock_control.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {
    boolean existsByCode(String code);
}
