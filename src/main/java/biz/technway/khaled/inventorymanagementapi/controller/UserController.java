package biz.technway.khaled.inventorymanagementapi.controller;

import biz.technway.khaled.inventorymanagementapi.dto.InventoryResponseDTO;
import biz.technway.khaled.inventorymanagementapi.dto.LoginRequestDTO;
import biz.technway.khaled.inventorymanagementapi.dto.UserResponseDTO;
import biz.technway.khaled.inventorymanagementapi.entity.User;
import biz.technway.khaled.inventorymanagementapi.service.InventoryService;
import biz.technway.khaled.inventorymanagementapi.service.UserService;
import biz.technway.khaled.inventorymanagementapi.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@Validated
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final InventoryService inventoryService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService, InventoryService inventoryService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.inventoryService = inventoryService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        UserResponseDTO responseDTO = userService.convertToDTO(createdUser);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        try {
            boolean isAuthenticated = userService.validateUserLogin(loginRequest.getEmail(), loginRequest.getPassword());
            if (isAuthenticated) {
                User user = userService.findByEmail(loginRequest.getEmail());
                UserResponseDTO userDTO = userService.convertToDTO(user);
                String token = jwtUtil.generateToken(loginRequest.getEmail(), userDTO.getId());

                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("id", String.valueOf(userDTO.getId()));
                response.put("username", userDTO.getUsername());
                response.put("name", userDTO.getName());
                response.put("email", userDTO.getEmail());
                response.put("photoPath", userDTO.getPhotoPath());

                return ResponseEntity.ok()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .body(response);
            }
        } catch (ResponseStatusException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getReason());
            return new ResponseEntity<>(errorResponse, e.getStatusCode());
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserProfile(@RequestHeader("Authorization") String authToken) {
        String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
        Long userId = jwtUtil.getUserIdFromToken(token);

        Optional<UserResponseDTO> userDTO = userService.getUserById(userId);
        return userDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            @RequestHeader("Authorization") String authToken,
            @RequestBody User userDetails) {
        String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
        Long userId = jwtUtil.getUserIdFromToken(token);

        User updatedUser = userService.updateUserWithPhoto(userId, userDetails);
        UserResponseDTO responseDTO = userService.convertToDTO(updatedUser);

        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteUserProfile(@RequestHeader("Authorization") String authToken) {
        String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
        Long userId = jwtUtil.getUserIdFromToken(token);

        userService.deleteUser(userId);
        return new ResponseEntity<>("Account deleted successfully", HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUserDTOs();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        Optional<UserResponseDTO> userDTO = userService.getUserById(id);
        return userDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        UserResponseDTO responseDTO = userService.convertToDTO(updatedUser);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody String newPassword) {
        userService.updatePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/inventories")
    public ResponseEntity<List<InventoryResponseDTO>> getUserInventories(@RequestHeader("Authorization") String authToken) {
        logger.info("Accessing GET /api/v1/users/inventories - Retrieving inventories for the logged-in user");
        try {
            // Extract the token and user ID
            String token = authToken.startsWith("Bearer ") ? authToken.substring(7) : authToken;
            Long userId = jwtUtil.getUserIdFromToken(token);

            if (userId == null) {
                throw new IllegalArgumentException("Invalid token: User ID not found");
            }

            // Fetch inventories for the user
            List<InventoryResponseDTO> inventories = inventoryService.getInventoriesByUserId(userId);
            if (inventories.isEmpty()) {
                logger.warn("No inventories found for user ID: {}", userId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            logger.info("Retrieved {} inventories for user ID: {}", inventories.size(), userId);
            return new ResponseEntity<>(inventories, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error retrieving inventories: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}