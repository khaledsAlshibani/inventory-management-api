package biz.technway.khaled.inventorymanagementapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "product", uniqueConstraints = {@UniqueConstraint(columnNames = "sku")})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotBlank(message = "SKU is required")
    @Column(name = "sku", nullable = false, unique = true, length = 50)
    private String sku;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "initial_quantity", nullable = false)
    private Integer initialQuantity;

    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "area", precision = 10, scale = 2)
    private BigDecimal area;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "inventory_id")
    private Inventory inventory;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "expiration_date")
    @Temporal(TemporalType.DATE)
    private Date expirationDate;

    @Column(name = "production_date")
    @Temporal(TemporalType.DATE)
    private Date productionDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Enum for Status
    public enum Status {
        AVAILABLE, UNAVAILABLE
    }

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
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

    public @NotBlank(message = "Name is required") String getName() {
        return name;
    }

    public void setName(@NotBlank(message = "Name is required") String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public @NotBlank(message = "SKU is required") String getSku() {
        return sku;
    }

    public void setSku(@NotBlank(message = "SKU is required") String sku) {
        this.sku = sku;
    }

    public @NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = false) BigDecimal getPrice() {
        return price;
    }

    public void setPrice(@NotNull(message = "Price is required") @DecimalMin(value = "0.0", inclusive = false) BigDecimal price) {
        this.price = price;
    }

    public @NotNull Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NotNull Integer quantity) {
        this.quantity = quantity;
    }

    public @NotNull Integer getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(@NotNull Integer initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public @DecimalMin(value = "0.0", inclusive = false) BigDecimal getArea() {
        return area;
    }

    public void setArea(@DecimalMin(value = "0.0", inclusive = false) BigDecimal area) {
        this.area = area;
    }

    public @NotNull Status getStatus() {
        return status;
    }

    public void setStatus(@NotNull Status status) {
        this.status = status;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public @NotNull Long getUserId() {
        return userId;
    }

    public void setUserId(@NotNull Long userId) {
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
