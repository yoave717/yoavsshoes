package com.shoestore.service.auth;

import com.shoestore.dto.auth.*;
import com.shoestore.dto.user.UserDto;
import com.shoestore.dto.user.UserMapper;
import com.shoestore.dto.user.UserDto.CreateUserDto;
import com.shoestore.entity.user.User;
import com.shoestore.exception.*;
import com.shoestore.repository.user.UserRepository;
import com.shoestore.util.JwtUtil;
import com.shoestore.util.LoggingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for handling authentication operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    /**
     * Register a new user
     */
    public AuthResponse register(CreateUserDto createUserDto) {
        log.info("Attempting to register new user with email: {}", LoggingUtil.sanitize(createUserDto.getEmail()));

        // Check if email already exists
        if (userRepository.existsByEmailIgnoreCase(createUserDto.getEmail())) {
            LoggingUtil.logSecurityEvent("REGISTRATION_FAILED", createUserDto.getEmail(), "Email already exists");
            throw new EmailAlreadyExistsException(createUserDto.getEmail());
        }

        // Create user entity
        User user = userMapper.toEntity(createUserDto);

        // Set password and encode it
        user.setPasswordHash(passwordEncoder.encode(createUserDto.getPassword()));    

        // Save user
        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail());

        // Update last login
        savedUser.setLastLogin(LocalDateTime.now());
        userRepository.save(savedUser);

        LoggingUtil.logUserAction(savedUser.getId(), "REGISTER", "User registered successfully");
        LoggingUtil.logSecurityEvent("REGISTRATION_SUCCESS", savedUser.getEmail(), "New user registered");

        log.info("User registered successfully with ID: {}", savedUser.getId());

        return createAuthResponse(savedUser, token, "Registration successful");
    }

    /**
     * Login user
     */
    public AuthResponse login(LoginRequestDto request) {
        log.info("Attempting login for user: {}", LoggingUtil.sanitize(request.getEmail()));

        try {
            // Find user
            User user = userRepository.findByEmailIgnoreCase(request.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

            // Check if account is locked
            if (user.isAccountLocked()) {
                LoggingUtil.logSecurityEvent("LOGIN_FAILED", request.getEmail(), "Account is locked");
                throw new UnauthorizedException("Account is locked. Please try again later.");
            }

            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // Reset failed login attempts on successful authentication
            user.resetFailedLoginAttempts();
            userRepository.save(user);

            // Generate JWT token
            String token = jwtUtil.generateToken(authentication);

            LoggingUtil.logUserAction(user.getId(), "LOGIN", "User logged in successfully");
            LoggingUtil.logSecurityEvent("LOGIN_SUCCESS", user.getEmail(), "Successful login");

            log.info("User logged in successfully: {}", user.getId());

            return createAuthResponse(user, token, "Login successful");

        } catch (AuthenticationException e) {
            handleFailedLogin(request.getEmail());
            LoggingUtil.logSecurityEvent("LOGIN_FAILED", request.getEmail(), "Invalid credentials");
            throw new BadCredentialsException("Invalid email or password");
        }
    }


    /**
     * Logout user (invalidate token)
     */
    public LogoutResponse logout(String token) {
        String username = jwtUtil.getUsernameFromToken(token);

        userRepository.findByEmailIgnoreCase(username)
                .ifPresent(user -> {
                    LoggingUtil.logUserAction(user.getId(), "LOGOUT", "User logged out");
                    log.info("User logged out: {}", user.getId());
                });

        return LogoutResponse.builder()
                .message("Logout successful")
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Private helper methods

    /**
     * Create authentication response
     */
    private AuthResponse createAuthResponse(User user, String token, String message) {
        long expirationMs = jwtUtil.getExpirationTime();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expirationMs / 1000);

        UserDto userDto = userMapper.toDto(user);
    
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresAt(expiresAt)
                .user(userDto)
                .message(message)
                .build();
    }

    /**
     * Handle failed login attempt
     */
    private void handleFailedLogin(String email) {
        userRepository.findByEmailIgnoreCase(email)
                .ifPresent(user -> {
                    user.incrementFailedLoginAttempts();
                    userRepository.save(user);

                    if (user.getFailedLoginAttempts() >= 5) {
                        LoggingUtil.logSecurityEvent("ACCOUNT_LOCKED", email,
                                "Account locked due to " + user.getFailedLoginAttempts() + " failed login attempts");
                        userRepository.lockUserAccount(user.getId(), LocalDateTime.now().plusMinutes(30));
                    }
                });
    }
}