package biz.technway.khaled.inventorymanagementapi.controller;

import biz.technway.khaled.inventorymanagementapi.dto.InventoryResponseDTO;
import biz.technway.khaled.inventorymanagementapi.entity.Inventory;
import biz.technway.khaled.inventorymanagementapi.service.InventoryService;
import biz.technway.khaled.inventorymanagementapi.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventories")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final JwtUtil jwtUtil;

    @Autowired
    public InventoryController(InventoryService inventoryService, JwtUtil jwtUtil) {
        this.inventoryService = inventoryService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(
            @RequestHeader("Authorization") String authToken,
            @Valid @RequestBody Inventory inventory) {

        logger.info("Accessing POST /api/v1/inventories - Creating new inventory");

        try {
            String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;

            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId != null) {
                inventory.setUserId(userId);

                Inventory createdInventory = inventoryService.createInventory(inventory);
                InventoryResponseDTO responseDTO = inventoryService.convertToDTO(createdInventory);

                logger.info("Inventory created successfully with ID: {}", responseDTO.getId());
                return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
            } else {
                throw new IllegalArgumentException("User ID not found in token");
            }
        } catch (Exception e) {
            logger.error("Error creating inventory: {}", e.getMessage());
            return new ResponseEntity<>(new InventoryResponseDTO("Failed to create inventory"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventories() {
        logger.info("Accessing GET /api/v1/inventories - Retrieving all inventories");
        try {
            List<InventoryResponseDTO> inventories = inventoryService.getAllInventories();
            if (inventories.isEmpty()) {
                logger.warn("No inventories found");
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            logger.info("Retrieved {} inventories", inventories.size());
            return new ResponseEntity<>(inventories, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving inventories: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> getInventoryById(@PathVariable Long id) {
        logger.info("Accessing GET /api/v1/inventories/{} - Retrieving inventory by ID", id);
        try {
            Optional<InventoryResponseDTO> inventoryDTO = inventoryService.getInventoryById(id);
            if (inventoryDTO.isPresent()) {
                logger.info("Inventory found with ID: {}", id);
                return new ResponseEntity<>(inventoryDTO.get(), HttpStatus.OK);
            } else {
                logger.warn("Inventory not found with ID: {}", id);
                return new ResponseEntity<>(new InventoryResponseDTO("Inventory not found with ID: " + id), HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error retrieving inventory with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new InventoryResponseDTO("Failed to retrieve inventory"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authToken,
            @Valid @RequestBody Inventory inventoryDetails) {

        logger.info("Accessing PUT /api/v1/inventories/{} - Updating inventory", id);

        try {
            String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;

            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId != null) {
                inventoryDetails.setUserId(userId);

                Inventory updatedInventory = inventoryService.updateInventory(id, inventoryDetails);
                InventoryResponseDTO responseDTO = inventoryService.convertToDTO(updatedInventory);

                logger.info("Inventory updated successfully with ID: {}", id);
                return new ResponseEntity<>(responseDTO, HttpStatus.OK);
            } else {
                throw new IllegalArgumentException("User ID not found in token");
            }
        } catch (ResponseStatusException e) {
            logger.warn("Inventory not found with ID: {}", id);
            return new ResponseEntity<>(new InventoryResponseDTO("Inventory not found with ID: " + id), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating inventory with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>(new InventoryResponseDTO("Failed to update inventory"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteInventory(@PathVariable Long id) {
        logger.info("Accessing DELETE /api/v1/inventories/{} - Deleting inventory", id);
        try {
            inventoryService.deleteInventory(id);
            logger.info("Inventory deleted successfully with ID: {}", id);
            return ResponseEntity.ok("Inventory deleted successfully");
        } catch (ResponseStatusException e) {
            logger.warn("Inventory not found with ID: {}", id);
            return new ResponseEntity<>("Inventory not found with ID: " + id, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting inventory with ID {}: {}", id, e.getMessage());
            return new ResponseEntity<>("Failed to delete inventory", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.info("Accessing GET /api/v1/inventories/statistics - Retrieving inventory statistics");
        try {
            Map<String, Object> statistics = inventoryService.getStatistics();
            logger.info("Successfully retrieved inventory statistics");
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving inventory statistics: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
