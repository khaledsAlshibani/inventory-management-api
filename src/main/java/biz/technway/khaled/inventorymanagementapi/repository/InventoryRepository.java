package biz.technway.khaled.inventorymanagementapi.repository;

import biz.technway.khaled.inventorymanagementapi.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByUserId(Long userId);

    List<Inventory> findByStatus(String status);

    List<Inventory> findByUserIdAndStatus(Long userId, String status);

    long countByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
