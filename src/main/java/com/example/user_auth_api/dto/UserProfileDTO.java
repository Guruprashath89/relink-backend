package com.example.user_auth_api.dto;

import java.time.LocalDateTime;

public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String city;
    private String profilePicture;
    private LocalDateTime joinedAt;
    private long totalDonated;
    private long totalReceived;
    private long completedDonations;
    private Double averageRating;
    private long totalRatings;

    public UserProfileDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public long getTotalDonated() { return totalDonated; }
    public void setTotalDonated(long totalDonated) { this.totalDonated = totalDonated; }

    public long getTotalReceived() { return totalReceived; }
    public void setTotalReceived(long totalReceived) { this.totalReceived = totalReceived; }

    public long getCompletedDonations() { return completedDonations; }
    public void setCompletedDonations(long completedDonations) { this.completedDonations = completedDonations; }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public long getTotalRatings() { return totalRatings; }
    public void setTotalRatings(long totalRatings) { this.totalRatings = totalRatings; }
}
