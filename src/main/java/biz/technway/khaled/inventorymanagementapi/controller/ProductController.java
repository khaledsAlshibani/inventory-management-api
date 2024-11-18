package biz.technway.khaled.inventorymanagementapi.controller;

import biz.technway.khaled.inventorymanagementapi.dto.ProductResponseDTO;
import biz.technway.khaled.inventorymanagementapi.entity.Product;
import biz.technway.khaled.inventorymanagementapi.service.ProductService;
import biz.technway.khaled.inventorymanagementapi.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {

    private final ProductService productService;
    private final JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    public ProductController(ProductService productService, JwtUtil jwtUtil) {
        this.productService = productService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            @RequestHeader("Authorization") String authToken,
            @Valid @RequestBody Product product) {

        logger.info("Accessing POST /api/v1/products - Creating new product");

        try {
            // Extract user ID from token
            String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId != null) {
                // Set user ID in product
                product.setUserId(userId);

                // Create product
                Product createdProduct = productService.createProduct(product);
                ProductResponseDTO responseDTO = productService.convertToDTO(createdProduct);

                logger.info("Product created successfully with ID: {}", responseDTO.getId());
                return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
            } else {
                throw new IllegalArgumentException("User ID not found in token");
            }
        } catch (Exception e) {
            logger.error("Error creating product: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        logger.info("Accessing GET /api/v1/products - Retrieving all products");
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        logger.info("Accessing GET /api/v1/products/{} - Retrieving product by ID", id);
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authToken,
            @Valid @RequestBody Product productDetails) {

        logger.info("Accessing PUT /api/v1/products/{} - Updating product", id);

        try {
            // Extract user ID from token
            String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId != null) {
                // Set user ID in product details
                productDetails.setUserId(userId);

                // Update product
                Product updatedProduct = productService.updateProduct(id, productDetails);
                ProductResponseDTO responseDTO = productService.convertToDTO(updatedProduct);

                logger.info("Product updated successfully with ID: {}", id);
                return ResponseEntity.ok(responseDTO);
            } else {
                throw new IllegalArgumentException("User ID not found in token");
            }
        } catch (Exception e) {
            logger.error("Error updating product with ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        logger.info("Accessing DELETE /api/v1/products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product deleted successfully.");
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        logger.info("Accessing GET /api/v1/products/statistics - Retrieving product statistics");
        try {
            Map<String, Object> statistics = productService.getStatistics();
            logger.info("Successfully retrieved product statistics");
            return new ResponseEntity<>(statistics, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving product statistics: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
