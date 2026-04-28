package com.example.user_auth_api.controller;

import com.example.user_auth_api.dto.UserProfileDTO;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.service.ProfileService;
import com.example.user_auth_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class ProfileController {

    @Autowired private ProfileService profileService;
    @Autowired private UserService userService;

    // GET /api/profile/me — current user's profile
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication auth) {
        try {
            User user = getUser(auth);
            return ResponseEntity.ok(profileService.getProfile(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/profile/{username} — any user's public profile
    @GetMapping("/{username}")
    public ResponseEntity<?> getProfile(@PathVariable String username) {
        try {
            return ResponseEntity.ok(profileService.getProfileByUsername(username));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // PUT /api/profile/me — update bio, city, profilePicture
    @PutMapping("/me")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> updates, Authentication auth) {
        try {
            User user = getUser(auth);
            UserProfileDTO updated = profileService.updateProfile(user, updates);
            return ResponseEntity.ok(Map.of("message", "Profile updated successfully", "profile", updated));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private User getUser(Authentication auth) {
        User u = userService.findByUsername(auth.getName());
        if (u == null) throw new RuntimeException("User not found");
        return u;
    }
}
