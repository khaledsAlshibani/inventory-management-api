package biz.technway.khaled.inventorymanagementapi.repository;

import biz.technway.khaled.inventorymanagementapi.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
