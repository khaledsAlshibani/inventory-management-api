package biz.technway.khaled.inventorymanagementapi.service;

import biz.technway.khaled.inventorymanagementapi.dto.ProductResponseDTO;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    public Product createProduct(Product product) {
        System.out.println("Received product: " + product);

        if (product.getInventoryId() != null) {
            System.out.println("Received inventoryId: " + product.getInventoryId());

            Inventory inventory = inventoryRepository.findById(product.getInventoryId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Inventory not found with ID: " + product.getInventoryId()));
            product.setInventory(inventory);
            System.out.println("Product after setting inventory: " + product);
        } else {
            System.out.println("No inventoryId provided for product.");
        }

        Product savedProduct = productRepository.save(product);
        System.out.println("Saved product: " + savedProduct);
        return savedProduct;
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ProductResponseDTO> getProductsByInventoryId(Long inventoryId) {
        return productRepository.findByInventoryId(inventoryId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id));
        return convertToDTO(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id));

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setSku(productDetails.getSku());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setQuantity(productDetails.getQuantity());
        existingProduct.setArea(productDetails.getArea());
        existingProduct.setStatus(productDetails.getStatus());
        existingProduct.setExpirationDate(productDetails.getExpirationDate());
        existingProduct.setProductionDate(productDetails.getProductionDate());

        return productRepository.save(existingProduct);
    }

    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + id));
        productRepository.delete(product);
    }

    public ProductResponseDTO convertToDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setSku(product.getSku());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setInitialQuantity(product.getInitialQuantity());
        dto.setArea(product.getArea());
        dto.setStatus(product.getStatus().name());
        dto.setInventoryId(product.getInventoryId()); // Include inventoryId
        dto.setUserId(product.getUserId());
        dto.setExpirationDate(product.getExpirationDate());
        dto.setProductionDate(product.getProductionDate());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
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

        // Create a HashMap to store all the key-value pairs
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalProducts", totalProducts);
        statistics.put("totalPriceValue", totalPriceValue);
        statistics.put("totalAreaUsed", totalAreaUsed);
        statistics.put("averagePrice", averagePrice);
        statistics.put("averageAreaUsed", averageAreaUsed);
        statistics.put("totalExpiredProducts", totalExpiredProducts);
        statistics.put("totalProductsWithoutInventories", totalProductsWithoutInventories);
        statistics.put("totalQuantity", totalQuantity);
        statistics.put("averageQuantityPerProduct", averageQuantityPerProduct);
        statistics.put("totalInitialQuantity", totalInitialQuantity);
        statistics.put("statusCounts", statusCounts);
        statistics.put("inventoryCounts", inventoryCounts);
        statistics.put("mostExpensiveProduct", mostExpensiveProduct.map(Product::getName).orElse("None"));
        statistics.put("leastExpensiveProduct", leastExpensiveProduct.map(Product::getName).orElse("None"));

        return statistics;
    }
}
