package biz.technway.khaled.inventorymanagementapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 65535, message = "Description is too long")
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.ACTIVE;

    @NotNull(message = "Inventory type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "inventory_type", nullable = false)
    private InventoryType inventoryType = InventoryType.WAREHOUSE;

    @Size(max = 255, message = "Address must be at most 255 characters")
    @Column(name = "address", length = 255)
    private String address;

    @NotNull(message = "Area is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Area must be greater than 0")
    @Column(name = "area", precision = 10, scale = 2, nullable = false)
    private BigDecimal area;

    /**
     * If not set, defaults to the value of `area` during creation.
     */
    @Column(name = "available_area", precision = 10, scale = 2)
    private BigDecimal availableArea;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    public enum Status {
        ACTIVE, INACTIVE
    }

    public enum InventoryType {
        WAREHOUSE, STORE, ONLINE
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        if (description == null) {
            description = "";
        }
        if (availableArea == null && area != null) {
            availableArea = area;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") @Size(max = 100, message = "Name must be at most 100 characters") String name) {
        this.name = name;
    }

    public @Size(max = 65535, message = "Description is too long") String getDescription() {
        return description;
    }

    public void setDescription(@Size(max = 65535, message = "Description is too long") String description) {
        this.description = description;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull(message = "User ID is required") Long userId) {
        this.userId = userId;
    }

    public @NotNull(message = "Status is required") Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status is required") Status status) {
        this.status = status;
    }

    public @NotNull(message = "Inventory type is required") InventoryType getInventoryType() {
        return inventoryType;
    }

    public void setInventoryType(@NotNull(message = "Inventory type is required") InventoryType inventoryType) {
        this.inventoryType = inventoryType;
    }

    public @Size(max = 255, message = "Address must be at most 255 characters") String getAddress() {
        return address;
    }

    public void setAddress(@Size(max = 255, message = "Address must be at most 255 characters") String address) {
        this.address = address;
    }

    public @NotNull(message = "Area is required") @DecimalMin(value = "0.0", inclusive = false, message = "Area must be greater than 0") BigDecimal getArea() {
        return area;
    }

    public void setArea(@NotNull(message = "Area is required") @DecimalMin(value = "0.0", inclusive = false, message = "Area must be greater than 0") BigDecimal area) {
        this.area = area;
    }

    public @DecimalMin(value = "0.0", inclusive = false, message = "Available area must be greater than 0") BigDecimal getAvailableArea() {
        return availableArea;
    }

    public void setAvailableArea(@DecimalMin(value = "0.0", inclusive = false, message = "Available area must be greater than 0") BigDecimal availableArea) {
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

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}