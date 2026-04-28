package com.example.user_auth_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "donation_requests")
public class DonationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "donation_id", nullable = false)
    @JsonIgnore
    private Donation donation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    @JsonIgnore
    private User requester;

    @Column(nullable = false)
    private String message; // Optional message from requester

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();

    @Column
    private LocalDateTime respondedAt;

    public enum RequestStatus {
        PENDING, ACCEPTED, DECLINED
    }

    public DonationRequest() {}

    public DonationRequest(Donation donation, User requester, String message) {
        this.donation = donation;
        this.requester = requester;
        this.message = message;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Donation getDonation() { return donation; }
    public void setDonation(Donation donation) { this.donation = donation; }

    public User getRequester() { return requester; }
    public void setRequester(User requester) { this.requester = requester; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
}
