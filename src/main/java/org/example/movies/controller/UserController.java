package org.example.movies.controller;

import org.example.movies.entity.user;
import org.example.movies.repository.userRepository;
import org.example.movies.dto.UserResponse;
import org.example.movies.dto.LoginRequest;
import org.example.movies.dto.LoginResponse;
import org.example.movies.config.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;
import org.example.movies.exception.EmailAlreadyExistsException;

import java.util.List;

import org.example.movies.dto.RegisterRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final userRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserController(
            userRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // Get All Users
    @GetMapping
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getRole()
                ))
                .toList();
    }

    // Get User By ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {

        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(
                        new UserResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getRole()
                        )
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Register User

    // Register User
    @PostMapping
    public UserResponse addUser(@Valid @RequestBody RegisterRequest request) {
        // Check if email is already registered
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        user newUser = new user();

        newUser.setName(request.getName());
        newUser.setEmail(request.getEmail());

        // Every newly registered user starts as CUSTOMER
        newUser.setRole("CUSTOMER");

        // Hash password
        newUser.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user savedUser = userRepository.save(newUser);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }

    // Login User
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest request) {

        user existingUser = userRepository.findByEmail(request.getEmail())
                .orElse(null);

        if (existingUser == null) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                existingUser.getPassword()
        );

        if (!passwordMatches) {
            return ResponseEntity.status(401)
                    .body("Invalid email or password");
        }

        String token = jwtService.generateToken(existingUser.getEmail());

        LoginResponse response = new LoginResponse(
                existingUser.getId(),
                existingUser.getName(),
                existingUser.getEmail(),
                existingUser.getRole(),
                token
        );

        return ResponseEntity.ok(response);
    }

    // Update User

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody user updatedUser,
            Authentication authentication) {

        // Identify logged-in user
        user currentUser = userRepository.findByEmail(authentication.getName())
                .orElse(null);

        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        // Prevent updating another user's account
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(403)
                    .body("You cannot update another user's profile");
        }

        // Check if the new email belongs to another user
        user existingUser = userRepository.findByEmail(updatedUser.getEmail())
                .orElse(null);

        if (existingUser != null && !existingUser.getId().equals(id)) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        currentUser.setName(updatedUser.getName());
        currentUser.setEmail(updatedUser.getEmail());

        // Update password only when provided
        if (updatedUser.getPassword() != null &&
                !updatedUser.getPassword().isBlank()) {

            currentUser.setPassword(
                    passwordEncoder.encode(updatedUser.getPassword())
            );
        }

        user savedUser = userRepository.save(currentUser);

        return ResponseEntity.ok(
                new UserResponse(
                        savedUser.getId(),
                        savedUser.getName(),
                        savedUser.getEmail(),
                        savedUser.getRole()
                )
        );
    }

    // Delete User
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
