package com.example.user_auth_api.service;

import com.example.user_auth_api.dto.DonationRequestDTO;
import com.example.user_auth_api.entity.*;
import com.example.user_auth_api.repository.DonationRepository;
import com.example.user_auth_api.repository.DonationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DonationRequestService {

    @Autowired
    private DonationRequestRepository requestRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private NotificationService notificationService;

    // User requests a donation
    @Transactional
    public DonationRequestDTO createRequest(Donation donation, User requester, String message) {
        // Can't request your own donation
        if (donation.getDonator().getId().equals(requester.getId())) {
            throw new RuntimeException("You cannot request your own donation");
        }
        // Donation must be AVAILABLE
        if (donation.getStatus() != Donation.DonationStatus.AVAILABLE) {
            throw new RuntimeException("This donation is no longer available");
        }
        // Check duplicate request
        requestRepository.findByDonationAndRequester(donation, requester).ifPresent(r -> {
            throw new RuntimeException("You have already requested this donation");
        });

        DonationRequest req = new DonationRequest(donation, requester, message != null ? message : "");
        DonationRequest saved = requestRepository.save(req);

        // Notify the donator
        notificationService.send(
            donation.getDonator(),
            "New Pickup Request",
            requester.getUsername() + " has requested your donation: " + donation.getFoodType(),
            Notification.NotificationType.REQUEST_RECEIVED,
            saved.getId()
        );

        return new DonationRequestDTO(saved);
    }

    // Donator accepts a request
    @Transactional
    public DonationRequestDTO acceptRequest(Long requestId, User donator) {
        DonationRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!req.getDonation().getDonator().getId().equals(donator.getId())) {
            throw new RuntimeException("Not authorized to respond to this request");
        }
        if (req.getStatus() != DonationRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request is no longer pending");
        }

        req.setStatus(DonationRequest.RequestStatus.ACCEPTED);
        req.setRespondedAt(LocalDateTime.now());
        requestRepository.save(req);

        // Update donation status to REQUESTED and set recipient
        Donation donation = req.getDonation();
        donation.setStatus(Donation.DonationStatus.REQUESTED);
        donation.setRecipient(req.getRequester());
        donationRepository.save(donation);

        // Decline all other pending requests for this donation
        List<DonationRequest> others = requestRepository.findByDonationAndStatus(
                donation, DonationRequest.RequestStatus.PENDING);
        others.forEach(other -> {
            other.setStatus(DonationRequest.RequestStatus.DECLINED);
            other.setRespondedAt(LocalDateTime.now());
            notificationService.send(
                other.getRequester(),
                "Request Declined",
                "Your request for " + donation.getFoodType() + " was not accepted this time.",
                Notification.NotificationType.REQUEST_DECLINED,
                other.getId()
            );
        });
        requestRepository.saveAll(others);

        // Notify the requester of acceptance
        notificationService.send(
            req.getRequester(),
            "Request Accepted! 🎉",
            donator.getUsername() + " accepted your request for " + donation.getFoodType() + ". Please pick up from: " + donation.getPickupLocation(),
            Notification.NotificationType.REQUEST_ACCEPTED,
            req.getId()
        );

        return new DonationRequestDTO(req);
    }

    // Donator declines a request
    @Transactional
    public DonationRequestDTO declineRequest(Long requestId, User donator) {
        DonationRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!req.getDonation().getDonator().getId().equals(donator.getId())) {
            throw new RuntimeException("Not authorized to respond to this request");
        }
        if (req.getStatus() != DonationRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Request is no longer pending");
        }

        req.setStatus(DonationRequest.RequestStatus.DECLINED);
        req.setRespondedAt(LocalDateTime.now());
        requestRepository.save(req);

        notificationService.send(
            req.getRequester(),
            "Request Declined",
            "Your request for " + req.getDonation().getFoodType() + " was declined.",
            Notification.NotificationType.REQUEST_DECLINED,
            req.getId()
        );

        return new DonationRequestDTO(req);
    }

    // Mark donation as completed
    @Transactional
    public void markCompleted(Long donationId, User donator) {
        Donation donation = donationRepository.findById(donationId)
                .orElseThrow(() -> new RuntimeException("Donation not found"));

        if (!donation.getDonator().getId().equals(donator.getId())) {
            throw new RuntimeException("Not authorized");
        }
        if (donation.getStatus() != Donation.DonationStatus.REQUESTED) {
            throw new RuntimeException("Donation must be in REQUESTED state to complete");
        }

        donation.setStatus(Donation.DonationStatus.COMPLETED);
        donationRepository.save(donation);

        // Notify both parties
        notificationService.send(
            donator,
            "Donation Completed ✅",
            "Your donation of " + donation.getFoodType() + " has been marked as completed.",
            Notification.NotificationType.DONATION_COMPLETED,
            donation.getId()
        );
        if (donation.getRecipient() != null) {
            notificationService.send(
                donation.getRecipient(),
                "Donation Completed ✅",
                "The donation of " + donation.getFoodType() + " from " + donator.getUsername() + " is now completed. Please leave a rating!",
                Notification.NotificationType.DONATION_COMPLETED,
                donation.getId()
            );
        }
    }

    // Get all requests for donations owned by this user
    public List<DonationRequestDTO> getRequestsForMyDonations(User donator) {
        List<Donation> myDonations = donationRepository.findByDonator(donator);
        return myDonations.stream()
                .flatMap(d -> requestRepository.findByDonation(d).stream())
                .map(DonationRequestDTO::new)
                .collect(Collectors.toList());
    }

    // Get all requests made by this user
    public List<DonationRequestDTO> getMyRequests(User requester) {
        return requestRepository.findByRequester(requester)
                .stream().map(DonationRequestDTO::new).collect(Collectors.toList());
    }
}
