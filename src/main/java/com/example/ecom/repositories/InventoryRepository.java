package com.example.ecom.repositories;

import com.example.ecom.models.Inventory;
import com.example.ecom.models.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findInventoryByProduct(Product product);
    Optional<Inventory> findByProduct_Id(long productId);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Inventory> findByProduct(Product product);
    void deleteByProduct_Id(long productId);
}
