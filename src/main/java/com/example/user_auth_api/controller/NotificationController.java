package com.example.user_auth_api.controller;

import com.example.user_auth_api.dto.NotificationDTO;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.service.NotificationService;
import com.example.user_auth_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired private NotificationService notificationService;
    @Autowired private UserService userService;

    // GET /api/notifications — all notifications for current user
    @GetMapping
    public ResponseEntity<?> getAll(Authentication auth) {
        try {
            User user = getUser(auth);
            List<NotificationDTO> list = notificationService.getForUser(user);
            long unread = notificationService.getUnreadCount(user);
            return ResponseEntity.ok(Map.of("notifications", list, "unreadCount", unread));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/notifications/unread-count
    @GetMapping("/unread-count")
    public ResponseEntity<?> unreadCount(Authentication auth) {
        try {
            User user = getUser(auth);
            return ResponseEntity.ok(Map.of("unreadCount", notificationService.getUnreadCount(user)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/notifications/mark-all-read
    @PostMapping("/mark-all-read")
    public ResponseEntity<?> markAllRead(Authentication auth) {
        try {
            User user = getUser(auth);
            notificationService.markAllRead(user);
            return ResponseEntity.ok(Map.of("message", "All notifications marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/notifications/{id}/read
    @PostMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable Long id, Authentication auth) {
        try {
            User user = getUser(auth);
            notificationService.markRead(id, user);
            return ResponseEntity.ok(Map.of("message", "Notification marked as read"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private User getUser(Authentication auth) {
        User u = userService.findByUsername(auth.getName());
        if (u == null) throw new RuntimeException("User not found");
        return u;
    }
}
