package biz.technway.khaled.inventorymanagementapi.repository;

import biz.technway.khaled.inventorymanagementapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByUserId(Long userId);
    List<Product> findByUserIdAndInventoryId(Long userId, Long inventoryId);
}
