package com.example.user_auth_api.controller;

import com.example.user_auth_api.dto.DonationDTO;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.repository.DonationRepository;
import com.example.user_auth_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DonationRepository donationRepository;

    // Get user's donated items
    @GetMapping("/donated")
    public ResponseEntity<?> getDonatedItems(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        List<DonationDTO> donated = donationRepository.findByDonator(user).stream()
                .map(DonationDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("message", "Donated items fetched", "donations", donated));
    }

    // Get user's received items (where recipient is the user)
    @GetMapping("/received")
    public ResponseEntity<?> getReceivedItems(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }
        List<DonationDTO> received = donationRepository.findByRecipient(user).stream()
                .map(DonationDTO::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("message", "Received items fetched", "donations", received));
    }
}