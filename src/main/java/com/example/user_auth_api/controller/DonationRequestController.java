package com.example.user_auth_api.controller;

import com.example.user_auth_api.dto.DonationRequestDTO;
import com.example.user_auth_api.entity.Donation;
import com.example.user_auth_api.entity.User;
import com.example.user_auth_api.repository.DonationRepository;
import com.example.user_auth_api.service.DonationRequestService;
import com.example.user_auth_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
@CrossOrigin(origins = "*")
public class DonationRequestController {

    @Autowired private DonationRequestService requestService;
    @Autowired private UserService userService;
    @Autowired private DonationRepository donationRepository;

    // POST /api/requests — create a request for a donation
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody Map<String, Object> body, Authentication auth) {
        try {
            User user = getUser(auth);
            Long donationId = Long.valueOf(body.get("donationId").toString());
            String message = body.containsKey("message") ? (String) body.get("message") : "";

            Donation donation = donationRepository.findById(donationId)
                    .orElseThrow(() -> new RuntimeException("Donation not found"));

            DonationRequestDTO dto = requestService.createRequest(donation, user, message);
            return ResponseEntity.ok(Map.of("message", "Request sent successfully", "request", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/requests/{id}/accept
    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id, Authentication auth) {
        try {
            User user = getUser(auth);
            DonationRequestDTO dto = requestService.acceptRequest(id, user);
            return ResponseEntity.ok(Map.of("message", "Request accepted", "request", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/requests/{id}/decline
    @PostMapping("/{id}/decline")
    public ResponseEntity<?> decline(@PathVariable Long id, Authentication auth) {
        try {
            User user = getUser(auth);
            DonationRequestDTO dto = requestService.declineRequest(id, user);
            return ResponseEntity.ok(Map.of("message", "Request declined", "request", dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // POST /api/requests/complete/{donationId} — mark donation as completed
    @PostMapping("/complete/{donationId}")
    public ResponseEntity<?> complete(@PathVariable Long donationId, Authentication auth) {
        try {
            User user = getUser(auth);
            requestService.markCompleted(donationId, user);
            return ResponseEntity.ok(Map.of("message", "Donation marked as completed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/requests/incoming — requests on MY donations
    @GetMapping("/incoming")
    public ResponseEntity<?> incoming(Authentication auth) {
        try {
            User user = getUser(auth);
            List<DonationRequestDTO> list = requestService.getRequestsForMyDonations(user);
            return ResponseEntity.ok(Map.of("requests", list));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // GET /api/requests/mine — requests I have made
    @GetMapping("/mine")
    public ResponseEntity<?> mine(Authentication auth) {
        try {
            User user = getUser(auth);
            List<DonationRequestDTO> list = requestService.getMyRequests(user);
            return ResponseEntity.ok(Map.of("requests", list));
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
