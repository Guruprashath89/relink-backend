package com.example.user_auth_api.service;

import com.example.user_auth_api.dto.DonationDTO;
import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.repository.DonationRepository;
import com.example.user_auth_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonationRepository donationRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<DonationDTO> getAllDonations() {
        List<Donation> donations = donationRepository.findAll();
        return donations.stream()
                .map(DonationDTO::new)
                .collect(Collectors.toList());
    }

    public void blockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsBlocked(true);
        userRepository.save(user);
    }

    public void unblockUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsBlocked(false);
        userRepository.save(user);
    }

    public Map<String, Object> getStats() {
        List<User> users = userRepository.findAll();
        long totalUsers = users.size();
        long blockedUsers = users.stream().filter(u -> Boolean.TRUE.equals(u.getIsBlocked())).count();
        long activeUsers = totalUsers - blockedUsers;
        long totalDonations = donationRepository.count();
        long available = donationRepository.countByStatus(com.example.user_auth_api.entity.Donation.DonationStatus.AVAILABLE);
        long requested = donationRepository.countByStatus(com.example.user_auth_api.entity.Donation.DonationStatus.REQUESTED);
        long completed = donationRepository.countByStatus(com.example.user_auth_api.entity.Donation.DonationStatus.COMPLETED);
        long removed = donationRepository.countByStatus(com.example.user_auth_api.entity.Donation.DonationStatus.REMOVED);

        Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("blockedUsers", blockedUsers);
        stats.put("totalDonations", totalDonations);
        stats.put("available", available);
        stats.put("requested", requested);
        stats.put("completed", completed);
        stats.put("removed", removed);
        return stats;
    }

    public void updateDonationStatus(Long donationId, Donation.DonationStatus status) {
        Donation donation = donationRepository.findById(donationId).orElseThrow(() -> new RuntimeException("Donation not found"));
        donation.setStatus(status);
        // NEW: If status is COMPLETED, set recipient (placeholder: assume admin user; in real app, set based on request)
        if (status == Donation.DonationStatus.COMPLETED) {
            // For demo, set recipient to a placeholder user (e.g., first user). In production, track requests.
            User recipient = userRepository.findAll().stream().findFirst().orElse(null);
            donation.setRecipient(recipient);
        }
        donationRepository.save(donation);
    }
}