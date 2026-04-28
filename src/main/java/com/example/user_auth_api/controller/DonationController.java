package com.example.user_auth_api.controller;

import com.example.user_auth_api.dto.DonationDTO;
import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.repository.DonationRepository;
import com.example.user_auth_api.service.DonationService;
import com.example.user_auth_api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/donations")
@CrossOrigin(origins = "*")
public class DonationController {

    private static final double MAX_PRICE = 500.0; // Price cap: ₹500

    @Autowired
    private DonationService donationService;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private UserService userService;

    // GET /api/donations — supports ?category=&location= query params
    @GetMapping
    public ResponseEntity<?> getDonations(
            Authentication authentication,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                User currentUser = userService.findByUsername(username);
                if (currentUser == null) {
                    response.put("error", "User not found");
                    return ResponseEntity.status(404).body(response);
                }

                List<Donation> donations;
                boolean hasCategory = category != null && !category.isBlank();
                boolean hasLocation = location != null && !location.isBlank();

                if (hasCategory && hasLocation) {
                    donations = donationRepository.findByDonatorNotAndFoodTypeAndLocation(currentUser, category, location);
                } else if (hasCategory) {
                    donations = donationRepository.findByDonatorNotAndFoodTypeContaining(currentUser, category);
                } else if (hasLocation) {
                    donations = donationRepository.findByDonatorNotAndPickupLocationContaining(currentUser, location);
                } else {
                    donations = donationService.getDonationsExceptUser(currentUser);
                }

                List<DonationDTO> donationDTOs = donations.stream()
                        .map(DonationDTO::new)
                        .collect(Collectors.toList());

                response.put("message", "Donations fetched successfully");
                response.put("donations", donationDTOs);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Public donations fetched");
                response.put("donations", new ArrayList<>());
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to fetch donations: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // POST /api/donations — creates donation with price validation
    @PostMapping
    public ResponseEntity<?> createDonation(@Valid @RequestBody Map<String, Object> donationData, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("error", "Unauthorized - Login required");
                return ResponseEntity.status(401).body(response);
            }

            String username = authentication.getName();
            User currentUser = userService.findByUsername(username);
            if (currentUser == null) {
                response.put("error", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            String foodType = (String) donationData.get("foodType");
            if (foodType == null || foodType.isBlank()) {
                response.put("error", "Food type is required");
                return ResponseEntity.badRequest().body(response);
            }

            Integer quantity = Integer.valueOf(donationData.get("quantity").toString());
            if (quantity < 1) {
                response.put("error", "Quantity must be at least 1");
                return ResponseEntity.badRequest().body(response);
            }

            String pickupLocation = (String) donationData.get("pickupLocation");
            if (pickupLocation == null || pickupLocation.isBlank()) {
                response.put("error", "Pickup location is required");
                return ResponseEntity.badRequest().body(response);
            }

            Double price = null;
            if (donationData.get("price") != null && !donationData.get("price").toString().isBlank()) {
                price = Double.valueOf(donationData.get("price").toString());
                // Price validation: must be between 0 and MAX_PRICE
                if (price < 0) {
                    response.put("error", "Price cannot be negative");
                    return ResponseEntity.badRequest().body(response);
                }
                if (price > MAX_PRICE) {
                    response.put("error", "Price exceeds maximum allowed amount of ₹" + MAX_PRICE + ". ReLink is for free or low-cost sharing only.");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            String image = (String) donationData.get("image");

            Donation donation = new Donation(foodType, quantity, pickupLocation, price, image, currentUser);
            Donation saved = donationService.createDonation(donation);

            response.put("message", "Donation listed successfully");
            response.put("donationId", saved.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to create donation: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}