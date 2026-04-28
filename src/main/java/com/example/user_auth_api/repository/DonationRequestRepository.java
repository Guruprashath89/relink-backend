package com.example.user_auth_api.repository;

import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.DonationRequest;
import com.example.user_auth_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonationRequestRepository extends JpaRepository<DonationRequest, Long> {

    // All requests for a specific donation (donator sees these)
    List<DonationRequest> findByDonation(Donation donation);

    // All requests made by a specific user
    List<DonationRequest> findByRequester(User requester);

    // Pending requests for a donation
    List<DonationRequest> findByDonationAndStatus(Donation donation, DonationRequest.RequestStatus status);

    // Check if user already requested this donation
    Optional<DonationRequest> findByDonationAndRequester(Donation donation, User requester);

    // Count pending requests for a donation
    long countByDonationAndStatus(Donation donation, DonationRequest.RequestStatus status);
}
