package com.example.user_auth_api.service;

import com.example.user_auth_api.dto.NotificationDTO;
import com.example.user_auth_api.entity.Notification;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public void send(User user, String title, String message, Notification.NotificationType type, Long referenceId) {
        Notification n = new Notification(user, title, message, type, referenceId);
        notificationRepository.save(n);
    }

    public List<NotificationDTO> getForUser(User user) {
        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream().map(NotificationDTO::new).collect(Collectors.toList());
    }

    public long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    public void markAllRead(User user) {
        List<Notification> unread = notificationRepository.findByUserAndIsReadFalseOrderByCreatedAtDesc(user);
        unread.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unread);
    }

    public void markRead(Long notificationId, User user) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getUser().getId().equals(user.getId())) {
                n.setIsRead(true);
                notificationRepository.save(n);
            }
        });
    }
}
