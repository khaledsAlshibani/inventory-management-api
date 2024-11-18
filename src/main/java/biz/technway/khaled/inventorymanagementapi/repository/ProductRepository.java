package biz.technway.khaled.inventorymanagementapi.repository;

import biz.technway.khaled.inventorymanagementapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.inventory.id = :inventoryId")
    List<Product> findByInventoryId(@Param("inventoryId") Long inventoryId);
}