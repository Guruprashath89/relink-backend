package com.example.user_auth_api.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "donations")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String foodType;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private String pickupLocation;

    @Column
    private Double price;  // Optional

    @Column(columnDefinition = "TEXT")  // For base64 image string
    private String image;  // Base64 string from frontend

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donator_id", nullable = false)
    @JsonIgnore  // Ignore donator in JSON to prevent circular reference
    private User donator;  // Links to User entity (donator)

    // NEW: Recipient field for tracking who received the donation
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id")
    @JsonIgnore  // Ignore recipient in JSON to prevent circular reference
    private User recipient;  // Set when status is COMPLETED

    @Column(nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "is_removed", nullable = false)
    private Boolean isRemoved = false;  // Default to false (not removed)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus status = DonationStatus.AVAILABLE;  // Default to Available

    public enum DonationStatus {
        AVAILABLE, REQUESTED, COMPLETED, REMOVED
    }

    // Constructors
    public Donation() {}

    public Donation(String foodType, Integer quantity, String pickupLocation, Double price, String image, User donator) {
        this.foodType = foodType;
        this.quantity = quantity;
        this.pickupLocation = pickupLocation;
        this.price = price;
        this.image = image;
        this.donator = donator;
        this.isRemoved = false;
        this.status = DonationStatus.AVAILABLE;
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

    public User getDonator() { return donator; }
    public void setDonator(User donator) { this.donator = donator; }

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Boolean getIsRemoved() { return isRemoved; }
    public void setIsRemoved(Boolean isRemoved) { this.isRemoved = isRemoved; }

    public DonationStatus getStatus() { return status; }
    public void setStatus(DonationStatus status) { this.status = status; }
}