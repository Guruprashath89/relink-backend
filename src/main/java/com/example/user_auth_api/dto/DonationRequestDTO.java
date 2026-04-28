package com.example.user_auth_api.dto;

import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.DonationRequest;
import java.time.LocalDateTime;

public class DonationRequestDTO {
    private Long id;
    private Long donationId;
    private String foodType;
    private String pickupLocation;
    private String requesterUsername;
    private String donatorUsername;
    private String message;
    private DonationRequest.RequestStatus status;
    private Donation.DonationStatus donationStatus; // AVAILABLE, REQUESTED, COMPLETED, REMOVED
    private LocalDateTime requestedAt;
    private LocalDateTime respondedAt;

    public DonationRequestDTO(DonationRequest req) {
        this.id = req.getId();
        this.donationId = req.getDonation() != null ? req.getDonation().getId() : null;
        this.foodType = req.getDonation() != null ? req.getDonation().getFoodType() : null;
        this.pickupLocation = req.getDonation() != null ? req.getDonation().getPickupLocation() : null;
        this.donationStatus = req.getDonation() != null ? req.getDonation().getStatus() : null;
        this.requesterUsername = req.getRequester() != null ? req.getRequester().getUsername() : null;
        this.donatorUsername = req.getDonation() != null && req.getDonation().getDonator() != null
                ? req.getDonation().getDonator().getUsername() : null;
        this.message = req.getMessage();
        this.status = req.getStatus();
        this.requestedAt = req.getRequestedAt();
        this.respondedAt = req.getRespondedAt();
    }

    public Long getId() { return id; }
    public Long getDonationId() { return donationId; }
    public String getFoodType() { return foodType; }
    public String getPickupLocation() { return pickupLocation; }
    public String getRequesterUsername() { return requesterUsername; }
    public String getDonatorUsername() { return donatorUsername; }
    public String getMessage() { return message; }
    public DonationRequest.RequestStatus getStatus() { return status; }
    public Donation.DonationStatus getDonationStatus() { return donationStatus; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getRespondedAt() { return respondedAt; }
}
