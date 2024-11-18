package biz.technway.khaled.inventorymanagementapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "product")
public class Product {

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

    @NotBlank(message = "SKU is required")
    @Size(max = 50, message = "SKU must be at most 50 characters")
    @Column(name = "sku", nullable = false, length = 50)
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @DecimalMin(value = "0.0", message = "Area cannot be negative")
    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status = Status.AVAILABLE;

    @Transient
    private Long inventoryId;

    @ManyToOne
    @JoinColumn(name = "inventory_id", referencedColumnName = "id", nullable = true)
    private Inventory inventory;

    @Column(name = "user_id", nullable = true)
    private Long userId;

    @Column(name = "expiration_date")
    @Temporal(TemporalType.DATE)
    private Date expirationDate;

    @Column(name = "production_date")
    @Temporal(TemporalType.DATE)
    private Date productionDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public enum Status {
        AVAILABLE, UNAVAILABLE
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        if (initialQuantity == null) {
            initialQuantity = quantity;
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

    public @NotBlank(message = "SKU is required") @Size(max = 50, message = "SKU must be at most 50 characters") String getSku() {
        return sku;
    }

    public void setSku(@NotBlank(message = "SKU is required") @Size(max = 50, message = "SKU must be at most 50 characters") String sku) {
        this.sku = sku;
    }

    public @NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0") BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0") BigDecimal price) {
        this.price = price;
    }

    public @NotNull(message = "Quantity is required") @Min(value = 0, message = "Quantity cannot be negative") Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull(message = "Quantity is required") @Min(value = 0, message = "Quantity cannot be negative") Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public @DecimalMin(value = "0.0", message = "Area cannot be negative") BigDecimal getArea() {
        return area;
    }

    public void setArea(@DecimalMin(value = "0.0", message = "Area cannot be negative") BigDecimal area) {
        this.area = area;
    }

    public @NotNull(message = "Status is required") Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull(message = "Status is required") Status status) {
        this.status = status;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Long getInventoryId() {
        return inventoryId != null ? inventoryId : (inventory != null ? inventory.getId() : null);
    }

    public void setInventoryId(Long inventoryId) {
        this.inventoryId = inventoryId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
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
}
