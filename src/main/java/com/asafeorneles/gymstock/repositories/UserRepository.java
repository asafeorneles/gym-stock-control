package com.asafeorneles.gymstock.repositories;

import com.asafeorneles.gymstock.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByRoles_Name(String roleName);
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
}
