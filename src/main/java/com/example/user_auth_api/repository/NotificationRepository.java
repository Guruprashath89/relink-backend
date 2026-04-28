package com.example.user_auth_api.repository;

import com.example.user_auth_api.entity.Notification;
import com.example.user_auth_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // All notifications for a user, newest first
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Unread notifications for a user
    List<Notification> findByUserAndIsReadFalseOrderByCreatedAtDesc(User user);

    // Count unread
    long countByUserAndIsReadFalse(User user);
}
