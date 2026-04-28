package com.example.user_auth_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    @JsonIgnore
    private Donation donation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rater_id", nullable = false)
    @JsonIgnore
    private User rater; // who gave the rating

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rated_user_id", nullable = false)
    @JsonIgnore
    private User ratedUser; // who is being rated

    @Column(nullable = false)
    private Integer stars; // 1-5

    @Column(length = 500)
    private String comment;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Rating() {}

    public Rating(Donation donation, User rater, User ratedUser, Integer stars, String comment) {
        this.donation = donation;
        this.rater = rater;
        this.ratedUser = ratedUser;
        this.stars = stars;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Donation getDonation() { return donation; }
    public void setDonation(Donation donation) { this.donation = donation; }

    public User getRater() { return rater; }
    public void setRater(User rater) { this.rater = rater; }

    public User getRatedUser() { return ratedUser; }
    public void setRatedUser(User ratedUser) { this.ratedUser = ratedUser; }

    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
