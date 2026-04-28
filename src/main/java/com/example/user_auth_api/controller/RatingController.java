package com.example.user_auth_api.controller;

import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.repository.UserRepository;
import com.example.user_auth_api.service.RatingService;
import com.example.user_auth_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    @Autowired private RatingService ratingService;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;

    // POST /api/ratings — submit a rating after a completed donation
    @PostMapping
    public ResponseEntity<?> submitRating(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            User rater = getUser(auth);
            Long donationId = Long.valueOf(body.get("donationId").toString());
            Integer stars = Integer.valueOf(body.get("stars").toString());
            String comment = body.containsKey("comment") ? (String) body.get("comment") : "";

            Map<String, Object> result = ratingService.submitRating(donationId, rater, stars, comment);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/ratings/user/{username} — get all ratings for a user
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getRatings(@PathVariable String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(ratingService.getRatingsForUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/ratings/my — get ratings for current user
    @GetMapping("/my")
    public ResponseEntity<?> getMyRatings(Authentication auth) {
        try {
            User user = getUser(auth);
            return ResponseEntity.ok(ratingService.getRatingsForUser(user));
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
