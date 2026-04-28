package com.example.user_auth_api.service;

import com.example.user_auth_api.entity.*;
import com.example.user_auth_api.repository.DonationRepository;
import com.example.user_auth_api.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private NotificationService notificationService;

    public Map<String, Object> submitRating(Long donationId, User rater, Integer stars, String comment) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        if (donation.getStatus() != Donation.DonationStatus.COMPLETED) {
            throw new RuntimeException("Can only rate completed donations");
        }

        // Determine who is being rated
        User ratedUser;
        if (rater.getId().equals(donation.getDonator().getId())) {
            // donator is rating the recipient
            ratedUser = donation.getRecipient();
        } else if (donation.getRecipient() != null && rater.getId().equals(donation.getRecipient().getId())) {
            // recipient is rating the donator
            ratedUser = donation.getDonator();
        } else {
            throw new RuntimeException("You are not part of this donation");
        }

        if (ratedUser == null) throw new RuntimeException("No recipient found for this donation");

        // Prevent duplicate rating
        ratingRepository.findByDonationAndRater(donation, rater).ifPresent(r -> {
            throw new RuntimeException("You have already rated this donation");
        });

        if (stars < 1 || stars > 5) throw new RuntimeException("Stars must be between 1 and 5");

        Rating rating = new Rating(donation, rater, ratedUser, stars, comment);
        ratingRepository.save(rating);

        notificationService.send(
            ratedUser,
            "New Rating Received ⭐",
            rater.getUsername() + " gave you " + stars + " star(s) for the donation: " + donation.getFoodType(),
            Notification.NotificationType.RATING_RECEIVED,
            donation.getId()
        );

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "Rating submitted successfully");
        result.put("stars", stars);
        result.put("ratedUser", ratedUser.getUsername());
        return result;
    }

    public Map<String, Object> getRatingsForUser(User user) {
        List<Rating> ratings = ratingRepository.findByRatedUser(user);
        Double avg = ratingRepository.findAverageRatingByUser(user);
        long count = ratingRepository.countByRatedUser(user);

        List<Map<String, Object>> list = ratings.stream().map(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", r.getId());
            m.put("stars", r.getStars());
            m.put("comment", r.getComment());
            m.put("raterUsername", r.getRater().getUsername());
            m.put("foodType", r.getDonation().getFoodType());
            m.put("createdAt", r.getCreatedAt());
            return m;
        }).collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ratings", list);
        result.put("averageRating", avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        result.put("totalRatings", count);
        return result;
    }
}
