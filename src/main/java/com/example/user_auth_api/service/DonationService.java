package com.example.user_auth_api.service;

import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;  // For seeding on startup

import java.util.ArrayList;
import java.util.List;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private UserService userService;  // To get test users

    // FIXED: Seed test data on startup (if DB empty)
    @PostConstruct
    public void initTestData() {
        List<Donation> existing = donationRepository.findAll();
        if (existing.isEmpty()) {
            // Create test users if needed (or assume they exist)
            User user1 = userService.findByUsername("testuser1");  // Replace with real usernames
            User user2 = userService.findByUsername("testuser2");

            if (user1 != null) {
                Donation d1 = new Donation("Prepared Meals", 10, "123 Main St", 0.0, null, user1);
                donationRepository.save(d1);
            }
            if (user2 != null) {
                Donation d2 = new Donation("Fresh Produce", 20, "456 Oak Ave", null, null, user2);
                donationRepository.save(d2);
            }
            System.out.println("Seeded test donations");  // Log to console
        }
    }

    public Donation createDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public List<Donation> getDonationsExceptUser(User currentUser) {
        if (currentUser == null) return new ArrayList<>();  // FIXED: Handle null user
        return donationRepository.findByDonatorNot(currentUser);  // Your repo method
    }
}
