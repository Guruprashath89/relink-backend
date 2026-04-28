package com.example.user_auth_api.repository;

import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {
    @Query("SELECT d FROM Donation d WHERE d.donator != :donator AND d.status = 'AVAILABLE'")
    List<Donation> findByDonatorNot(@Param("donator") User donator);

    List<Donation> findByDonator(User donator);
    List<Donation> findByRecipient(User recipient);

    // Category filter
    @Query("SELECT d FROM Donation d WHERE d.donator != :donator AND d.status = 'AVAILABLE' AND LOWER(d.foodType) LIKE LOWER(CONCAT('%', :category, '%'))")
    List<Donation> findByDonatorNotAndFoodTypeContaining(@Param("donator") User donator, @Param("category") String category);

    // Location filter
    @Query("SELECT d FROM Donation d WHERE d.donator != :donator AND d.status = 'AVAILABLE' AND LOWER(d.pickupLocation) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Donation> findByDonatorNotAndPickupLocationContaining(@Param("donator") User donator, @Param("location") String location);

    // Category + location combined
    @Query("SELECT d FROM Donation d WHERE d.donator != :donator AND d.status = 'AVAILABLE' AND LOWER(d.foodType) LIKE LOWER(CONCAT('%', :category, '%')) AND LOWER(d.pickupLocation) LIKE LOWER(CONCAT('%', :location, '%'))")
    List<Donation> findByDonatorNotAndFoodTypeAndLocation(@Param("donator") User donator, @Param("category") String category, @Param("location") String location);

    // Count helpers for admin stats
    long countByStatus(Donation.DonationStatus status);
}