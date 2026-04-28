package com.example.user_auth_api.service;

import com.example.user_auth_api.dto.UserProfileDTO;
import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.repository.DonationRepository;
import com.example.user_auth_api.repository.RatingRepository;
import com.example.user_auth_api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private RatingRepository ratingRepository;

    public UserProfileDTO getProfile(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setCity(user.getCity());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setJoinedAt(user.getJoinedAt());

        dto.setTotalDonated(donationRepository.findByDonator(user).size());
        dto.setTotalReceived(donationRepository.findByRecipient(user).size());
        dto.setCompletedDonations(donationRepository.findByDonator(user).stream()
                .filter(d -> d.getStatus() == Donation.DonationStatus.COMPLETED).count());

        Double avg = ratingRepository.findAverageRatingByUser(user);
        dto.setAverageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : null);
        dto.setTotalRatings(ratingRepository.countByRatedUser(user));

        return dto;
    }

    public UserProfileDTO getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return getProfile(user);
    }

    public UserProfileDTO updateProfile(User user, Map<String, Object> updates) {
        if (updates.containsKey("bio")) user.setBio((String) updates.get("bio"));
        if (updates.containsKey("city")) user.setCity((String) updates.get("city"));
        if (updates.containsKey("profilePicture")) user.setProfilePicture((String) updates.get("profilePicture"));
        userRepository.save(user);
        return getProfile(user);
    }
}
