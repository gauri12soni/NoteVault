package com.gauri.noteVault.service;

import com.gauri.noteVault.dto.AuthRequest;
import com.gauri.noteVault.dto.AuthResponse;
import com.gauri.noteVault.dto.RegisterRequest;
import com.gauri.noteVault.entity.User;
import com.gauri.noteVault.repository.UserRepository;
import com.gauri.noteVault.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    // Handles user registration and returns JWT token after successful registration
    public AuthResponse register(RegisterRequest req) {
        String username = req.getUsername().trim();
        logger.info("Attempting registration for username: {}", username);

        if (userRepository.existsByUsername(username)) {
            logger.warn("Registration failed - username already exists: {}", username);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(req.getPassword()))
                .roles("USER")
                .fullName(req.getFullName())
                .build();

        userRepository.save(user);
        logger.info("User registered successfully: {}", username);

        String token = jwtUtil.generateToken(user.getUsername());
        logger.debug("JWT generated for user: {}", username);

        return new AuthResponse(
                "User registered successfully",
                user.getUsername(),
                token
        );
    }

    // Handles user login and returns a new JWT token
    public AuthResponse login(AuthRequest req) {
        String username = req.getUsername().trim();
        logger.info("Login attempt for username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Login failed - user not found: {}", username);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
                });

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            logger.warn("Login failed - invalid password for user: {}", username);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        logger.info("Login successful for user: {}", username);
        logger.debug("JWT generated for user: {}", username);

        return new AuthResponse(
                "Login successful",
                user.getUsername(),
                token
        );
    }
}
