package biz.technway.khaled.inventorymanagementapi.dto;

import biz.technway.khaled.inventorymanagementapi.entity.Inventory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class InventoryResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String status;
    private String inventoryType;
    private String address;
    private BigDecimal area;
    private BigDecimal availableArea;
    private Date createdAt;
    private Date updatedAt;

    private String message;

    public InventoryResponseDTO() {}

    public InventoryResponseDTO(String message) {
        this.message = message;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(String inventoryType) {
        this.inventoryType = inventoryType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getArea() {
        return area;
    }

    public void setArea(BigDecimal area) {
        this.area = area;
    }

    public BigDecimal getAvailableArea() {
        return availableArea;
    }

    public void setAvailableArea(BigDecimal availableArea) {
        this.availableArea = availableArea;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
