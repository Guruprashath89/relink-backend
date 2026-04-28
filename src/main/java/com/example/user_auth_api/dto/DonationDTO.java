package com.example.user_auth_api.dto;

import com.example.user_auth_api.entity.Donation;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class DonationDTO {
    private Long id;
    private String foodType;
    private Integer quantity;
    private String pickupLocation;
    private Double price;
    private String image;
    private String donatorUsername;
    private String recipientUsername;  // NEW: Include recipient
    private LocalDateTime timestamp;
    private Boolean isRemoved;
    private Donation.DonationStatus status;

    public DonationDTO(Donation donation) {
        this.id = donation.getId();
        this.foodType = donation.getFoodType();
        this.quantity = donation.getQuantity();
        this.pickupLocation = donation.getPickupLocation();
        this.price = donation.getPrice();
        this.image = donation.getImage();
        this.donatorUsername = donation.getDonator() != null ? donation.getDonator().getUsername() : null;
        this.recipientUsername = donation.getRecipient() != null ? donation.getRecipient().getUsername() : null;  // NEW
        this.timestamp = donation.getTimestamp();
        this.isRemoved = donation.getIsRemoved();
        this.status = donation.getStatus();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFoodType() { return foodType; }
    public void setFoodType(String foodType) { this.foodType = foodType; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getDonatorUsername() { return donatorUsername; }
    public void setDonatorUsername(String donatorUsername) { this.donatorUsername = donatorUsername; }

    public String getRecipientUsername() { return recipientUsername; }  // NEW
    public void setRecipientUsername(String recipientUsername) { this.recipientUsername = recipientUsername; }  // NEW

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Boolean getIsRemoved() { return isRemoved; }
    public void setIsRemoved(Boolean isRemoved) { this.isRemoved = isRemoved; }

    public Donation.DonationStatus getStatus() { return status; }
    public void setStatus(Donation.DonationStatus status) { this.status = status; }
}