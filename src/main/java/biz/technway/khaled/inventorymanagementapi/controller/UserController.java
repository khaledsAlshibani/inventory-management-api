package biz.technway.khaled.inventorymanagementapi.controller;

import biz.technway.khaled.inventorymanagementapi.dto.LoginRequestDTO;
import biz.technway.khaled.inventorymanagementapi.dto.UserResponseDTO;
import biz.technway.khaled.inventorymanagementapi.entity.User;
import biz.technway.khaled.inventorymanagementapi.service.UserService;
import biz.technway.khaled.inventorymanagementapi.util.JwtUtil;
import jakarta.validation.Valid;
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
@Validated // Optional, to enforce validation in the controller level
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
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
                String token = jwtUtil.generateToken(loginRequest.getEmail());
                User user = userService.findByEmail(loginRequest.getEmail()); // Fetch the user details
                UserResponseDTO userDTO = userService.convertToDTO(user);

                Map<String, String> response = new HashMap<>();
                response.put("message", "Login successful");
                response.put("token", token);
                response.put("id", String.valueOf(userDTO.getId()));
                response.put("name", userDTO.getName());
                response.put("email", userDTO.getEmail());

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
}
