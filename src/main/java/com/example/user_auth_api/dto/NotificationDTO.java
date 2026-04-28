package com.example.user_auth_api.dto;

import com.example.user_auth_api.entity.Notification;
import java.time.LocalDateTime;

public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private Notification.NotificationType type;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private Long referenceId;

    public NotificationDTO(Notification n) {
        this.id = n.getId();
        this.title = n.getTitle();
        this.message = n.getMessage();
        this.type = n.getType();
        this.isRead = n.getIsRead();
        this.createdAt = n.getCreatedAt();
        this.referenceId = n.getReferenceId();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Notification.NotificationType getType() { return type; }
    public Boolean getIsRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Long getReferenceId() { return referenceId; }
}
