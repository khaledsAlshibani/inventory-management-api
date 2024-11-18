package biz.technway.khaled.inventorymanagementapi.service;

import biz.technway.khaled.inventorymanagementapi.dto.InventoryResponseDTO;
import biz.technway.khaled.inventorymanagementapi.entity.Inventory;
import biz.technway.khaled.inventorymanagementapi.entity.Product;
import biz.technway.khaled.inventorymanagementapi.repository.InventoryRepository;
import biz.technway.khaled.inventorymanagementapi.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public List<InventoryResponseDTO> getAllInventories() {
        return inventoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<InventoryResponseDTO> getInventoriesByUserId(Long userId) {
        List<Inventory> inventories = inventoryRepository.findByUserId(userId);
        return inventories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<InventoryResponseDTO> getInventoryById(Long id) {
        return inventoryRepository.findById(id).map(this::convertToDTO);
    }

    public Inventory updateInventory(Long id, Inventory inventoryDetails) {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found with ID: " + id));

        existingInventory.setName(inventoryDetails.getName());
        existingInventory.setDescription(inventoryDetails.getDescription());
        existingInventory.setUserId(inventoryDetails.getUserId());
        existingInventory.setStatus(inventoryDetails.getStatus());
        existingInventory.setInventoryType(inventoryDetails.getInventoryType());
        existingInventory.setAddress(inventoryDetails.getAddress());
        existingInventory.setArea(inventoryDetails.getArea());
        existingInventory.setAvailableArea(inventoryDetails.getAvailableArea());

        return inventoryRepository.save(existingInventory);
    }

    public void deleteInventory(Long id) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory not found with ID: " + id));
        inventoryRepository.delete(inventory);
    }

    public InventoryResponseDTO convertToDTO(Inventory inventory) {
        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setId(inventory.getId());
        dto.setName(inventory.getName());
        dto.setDescription(inventory.getDescription());
        dto.setStatus(inventory.getStatus().name());
        dto.setInventoryType(inventory.getInventoryType().name());
        dto.setAddress(inventory.getAddress());
        dto.setArea(inventory.getArea());
        dto.setAvailableArea(inventory.getAvailableArea());
        dto.setCreatedAt(inventory.getCreatedAt());
        dto.setUpdatedAt(inventory.getUpdatedAt());
        return dto;
    }

    public Map<String, Object> getStatistics() {
        List<Product> products = productRepository.findAll();

        long totalProducts = products.size();
        BigDecimal totalPriceValue = products.stream()
                .map(product -> product.getPrice().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAreaUsed = products.stream()
                .map(Product::getArea)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> statusCounts = products.stream()
                .collect(Collectors.groupingBy(
                        product -> product.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Long> inventoryCounts = products.stream()
                .filter(product -> product.getInventory() != null)
                .collect(Collectors.groupingBy(
                        product -> product.getInventory().getName(),
                        Collectors.counting()
                ));

        BigDecimal averagePrice = totalProducts > 0
                ? products.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(totalProducts), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal averageAreaUsed = totalProducts > 0
                ? totalAreaUsed.divide(BigDecimal.valueOf(totalProducts), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalExpiredProducts = products.stream()
                .filter(product -> product.getExpirationDate() != null &&
                        product.getExpirationDate().before(new Date()))
                .count();

        long totalProductsWithoutInventories = products.stream()
                .filter(product -> product.getInventory() == null)
                .count();

        long totalQuantity = products.stream()
                .mapToLong(Product::getQuantity)
                .sum();

        BigDecimal averageQuantityPerProduct = totalProducts > 0
                ? BigDecimal.valueOf(totalQuantity)
                .divide(BigDecimal.valueOf(totalProducts), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalInitialQuantity = products.stream()
                .mapToLong(Product::getInitialQuantity)
                .sum();

        Optional<Product> mostExpensiveProduct = products.stream()
                .max(Comparator.comparing(Product::getPrice));

        Optional<Product> leastExpensiveProduct = products.stream()
                .min(Comparator.comparing(Product::getPrice));

        return Map.ofEntries(
                Map.entry("totalProducts", totalProducts),
                Map.entry("totalPriceValue", totalPriceValue),
                Map.entry("totalAreaUsed", totalAreaUsed),
                Map.entry("averagePrice", averagePrice),
                Map.entry("averageAreaUsed", averageAreaUsed),
                Map.entry("statusCounts", statusCounts),
                Map.entry("inventoryCounts", inventoryCounts),
                Map.entry("totalExpiredProducts", totalExpiredProducts),
                Map.entry("totalProductsWithoutInventories", totalProductsWithoutInventories),
                Map.entry("totalQuantity", totalQuantity),
                Map.entry("averageQuantityPerProduct", averageQuantityPerProduct),
                Map.entry("totalInitialQuantity", totalInitialQuantity),
                Map.entry("mostExpensiveProduct", mostExpensiveProduct.map(Product::getName).orElse("None")),
                Map.entry("leastExpensiveProduct", leastExpensiveProduct.map(Product::getName).orElse("None"))
        );
    }
}