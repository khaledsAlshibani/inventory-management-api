package biz.technway.khaled.inventorymanagementapi.service;

import biz.technway.khaled.inventorymanagementapi.entity.Inventory;
import biz.technway.khaled.inventorymanagementapi.entity.Product;
import biz.technway.khaled.inventorymanagementapi.entity.User;
import biz.technway.khaled.inventorymanagementapi.repository.InventoryRepository;
import biz.technway.khaled.inventorymanagementapi.repository.ProductRepository;
import biz.technway.khaled.inventorymanagementapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final InventoryRepository inventoryRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final ProductRepository productRepository;

    @Autowired
    public UserService(UserRepository userRepository, InventoryRepository inventoryRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }

    public User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Product> getUserProductsInInventory(Long userId, Long inventoryId) {
        // Ensure user exists
        userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + userId));

        // Fetch products by userId and inventoryId
        return productRepository.findByUserIdAndInventoryId(userId, inventoryId);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean validateUserLogin(String email, String rawPassword) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && passwordEncoder.matches(rawPassword, user.get().getPassword());
    }

    public List<Inventory> getUserInventories(Long userId) {
        return inventoryRepository.findByUserId(userId);
    }

    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setName(userDetails.getName());
        existingUser.setPhotoPath(userDetails.getPhotoPath());
        existingUser.setBirthdate(userDetails.getBirthdate());

        return userRepository.save(existingUser);
    }

    public void updatePassword(Long id, String newPassword) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));

        String hashedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(hashedPassword);
        userRepository.save(existingUser);
    }
}
