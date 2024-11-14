package biz.technway.khaled.inventorymanagementapi.repository;

import biz.technway.khaled.inventorymanagementapi.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}