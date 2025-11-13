package com.gauri.noteVault.controller;

import com.gauri.noteVault.dto.AuthRequest;
import com.gauri.noteVault.dto.AuthResponse;
import com.gauri.noteVault.dto.RegisterRequest;
import com.gauri.noteVault.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register a new user
    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        logger.info("Register API called for username: {}", request.getUsername());
        AuthResponse response = authService.register(request);
        logger.info("User '{}' registered successfully", request.getUsername());
        return response;
    }

    // Login an existing user
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        logger.info("Login API called for username: {}", request.getUsername());
        AuthResponse response = authService.login(request);
        logger.info("User '{}' logged in successfully", request.getUsername());
        return response;
    }
}
