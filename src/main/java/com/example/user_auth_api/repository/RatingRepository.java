package com.example.user_auth_api.repository;

import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.Rating;
import com.example.user_auth_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    // All ratings for a user (their profile)
    List<Rating> findByRatedUser(User ratedUser);

    // Average rating for a user
    @Query("SELECT AVG(r.stars) FROM Rating r WHERE r.ratedUser = :user")
    Double findAverageRatingByUser(@Param("user") User user);

    // Count ratings for a user
    long countByRatedUser(User ratedUser);

    // Check if rater already rated this donation
    Optional<Rating> findByDonationAndRater(Donation donation, User rater);

    // All ratings given by a user
    List<Rating> findByRater(User rater);
}
