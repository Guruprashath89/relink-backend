package com.example.user_auth_api.controller;

import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.service.UserService;
import com.example.user_auth_api.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User userRequest) {
        try {
            User savedUser = userService.registerUser(userRequest);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully: " + savedUser.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            Map<String, String> response = new HashMap<>();
            response.put("message", "Welcome, " + username + "!");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtil.generateToken(userDetails);

            // Fetch user from DB to get role
            User user = userService.findByUsername(username);
            String role = user.getRole();  // e.g., "ADMIN" or "USER"

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful: " + userDetails.getUsername());
            response.put("token", jwt);
            response.put("username", userDetails.getUsername());
            response.put("role", role);  // Include role for frontend redirection
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Login failed: " + e.getMessage()));
        }
    }
}