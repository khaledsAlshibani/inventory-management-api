package biz.technway.khaled.inventorymanagementapi.controller;

import biz.technway.khaled.inventorymanagementapi.dto.InventoryResponseDTO;
import biz.technway.khaled.inventorymanagementapi.entity.Inventory;
import biz.technway.khaled.inventorymanagementapi.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventories")
@Validated
public class InventoryController {

    private final InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> createInventory(@Valid @RequestBody Inventory inventory) {
        Inventory createdInventory = inventoryService.createInventory(inventory);
        InventoryResponseDTO responseDTO = inventoryService.convertToDTO(createdInventory);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InventoryResponseDTO>> getAllInventories() {
        List<InventoryResponseDTO> inventories = inventoryService.getAllInventories();
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> getInventoryById(@PathVariable Long id) {
        Optional<InventoryResponseDTO> inventoryDTO = inventoryService.getInventoryById(id);
        return inventoryDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> updateInventory(@PathVariable Long id, @Valid @RequestBody Inventory inventoryDetails) {
        Inventory updatedInventory = inventoryService.updateInventory(id, inventoryDetails);
        InventoryResponseDTO responseDTO = inventoryService.convertToDTO(updatedInventory);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }
}
