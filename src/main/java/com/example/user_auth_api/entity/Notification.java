package com.example.user_auth_api.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user; // recipient of notification

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private Long referenceId; // e.g. donationId or requestId for navigation

    public enum NotificationType {
        REQUEST_RECEIVED,   // donator gets this when someone requests their donation
        REQUEST_ACCEPTED,   // requester gets this when donator accepts
        REQUEST_DECLINED,   // requester gets this when donator declines
        DONATION_COMPLETED, // both parties when completed
        ACCOUNT_BLOCKED,    // user gets this when admin blocks them
        RATING_RECEIVED,    // user gets this when they receive a rating
        GENERAL
    }

    public Notification() {}

    public Notification(User user, String title, String message, NotificationType type, Long referenceId) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.referenceId = referenceId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
}
