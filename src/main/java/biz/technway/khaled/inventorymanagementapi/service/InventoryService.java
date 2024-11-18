package biz.technway.khaled.inventorymanagementapi.service;

import biz.technway.khaled.inventorymanagementapi.dto.InventoryResponseDTO;
import biz.technway.khaled.inventorymanagementapi.entity.Inventory;
import biz.technway.khaled.inventorymanagementapi.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
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
        List<Inventory> inventories = inventoryRepository.findAll();

        long totalInventories = inventories.size();
        BigDecimal totalArea = inventories.stream()
                .map(Inventory::getArea)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAvailableArea = inventories.stream()
                .map(Inventory::getAvailableArea)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> statusCounts = inventories.stream()
                .collect(Collectors.groupingBy(
                        inventory -> inventory.getStatus().name(),
                        Collectors.counting()
                ));

        Map<String, Long> typeCounts = inventories.stream()
                .collect(Collectors.groupingBy(
                        inventory -> inventory.getInventoryType().name(),
                        Collectors.counting()
                ));

        BigDecimal averageUtilization = inventories.stream()
                .map(inventory -> {
                    BigDecimal utilization = inventory.getAvailableArea()
                            .divide(inventory.getArea(), 2, RoundingMode.HALF_UP);
                    return BigDecimal.ONE.subtract(utilization);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(new BigDecimal(totalInventories), 2, RoundingMode.HALF_UP);

        return Map.of(
                "totalInventories", totalInventories,
                "totalArea", totalArea,
                "totalAvailableArea", totalAvailableArea,
                "statusCounts", statusCounts,
                "typeCounts", typeCounts,
                "averageUtilization", averageUtilization
        );
    }
}