package biz.technway.khaled.inventorymanagementapi.controller;

import biz.technway.khaled.inventorymanagementapi.dto.LoginRequestDTO;
import biz.technway.khaled.inventorymanagementapi.entity.Inventory;
import biz.technway.khaled.inventorymanagementapi.entity.Product;
import biz.technway.khaled.inventorymanagementapi.entity.User;
import biz.technway.khaled.inventorymanagementapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        boolean isAuthenticated = userService.validateUserLogin(loginRequest.getEmail(), loginRequest.getPassword());
        return isAuthenticated ? new ResponseEntity<>("Login successful", HttpStatus.OK)
                : new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        return user.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{userId}/inventories")
    public ResponseEntity<List<Inventory>> getUserInventories(@PathVariable Long userId) {
        List<Inventory> inventories = userService.getUserInventories(userId);
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }

    @GetMapping("/{userId}/inventories/{inventoryId}/products")
    public ResponseEntity<List<Product>> getUserProductsInInventory(
            @PathVariable Long userId, @PathVariable Long inventoryId) {

        List<Product> products = userService.getUserProductsInInventory(userId, inventoryId);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // Update User (without password)
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // Update Password
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        userService.updatePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }
}
