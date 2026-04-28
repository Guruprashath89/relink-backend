package com.example.user_auth_api.controller;

import com.example.user_auth_api.dto.DonationDTO;
import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = adminService.getAllUsers();
            return ResponseEntity.ok(Map.of("message", "Users fetched successfully", "users", users));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch users: " + e.getMessage()));
        }
    }

    @GetMapping("/donations")
    public ResponseEntity<?> getAllDonations() {
        try {
            List<DonationDTO> donations = adminService.getAllDonations();
            return ResponseEntity.ok(Map.of("message", "Donations fetched successfully", "donations", donations));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch donations: " + e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long userId) {
        try {
            adminService.blockUser(userId);
            return ResponseEntity.ok(Map.of("message", "User blocked successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to block user: " + e.getMessage()));
        }
    }

    @PostMapping("/users/{userId}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long userId) {
        try {
            adminService.unblockUser(userId);
            return ResponseEntity.ok(Map.of("message", "User unblocked successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to unblock user: " + e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try {
            return ResponseEntity.ok(adminService.getStats());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch stats: " + e.getMessage()));
        }
    }

    // NEW: Endpoint to update donation status
    @PostMapping("/donations/{donationId}/status")
    public ResponseEntity<?> updateDonationStatus(@PathVariable Long donationId, @RequestBody Map<String, String> body) {
        try {
            Donation.DonationStatus status = Donation.DonationStatus.valueOf(body.get("status").toUpperCase());
            adminService.updateDonationStatus(donationId, status);
            return ResponseEntity.ok(Map.of("message", "Donation status updated to " + status));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to update donation status: " + e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        try {
            List<User> users = adminService.getAllUsers();
            List<DonationDTO> donations = adminService.getAllDonations();
            return ResponseEntity.ok(Map.of(
                    "message", "Dashboard data fetched successfully",
                    "users", users,
                    "donations", donations
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Failed to fetch dashboard data: " + e.getMessage()));
        }
    }
}