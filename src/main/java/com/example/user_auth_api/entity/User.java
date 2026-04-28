package com.example.user_auth_api.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role = "USER";  // Default role for regular users; set to "ADMIN" for admins

    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked = false;

    // Profile fields
    @Column(length = 200)
    private String bio;

    @Column(length = 100)
    private String city;

    @Column(name = "profile_picture", columnDefinition = "TEXT")
    private String profilePicture; // base64

    @Column(name = "joined_at", nullable = false)
    private java.time.LocalDateTime joinedAt = java.time.LocalDateTime.now();

    // Existing: Donated items (donations made by this user)
    @OneToMany(mappedBy = "donator", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Donation> donations = new ArrayList<>();

    // NEW: Received items (donations received by this user)
    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Donation> receivedDonations = new ArrayList<>();

    // Constructors
    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public java.time.LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(java.time.LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public List<Donation> getDonations() {
        return donations;
    }

    public void setDonations(List<Donation> donations) {
        this.donations = donations;
    }

    // NEW: Getters and Setters for received donations
    public List<Donation> getReceivedDonations() {
        return receivedDonations;
    }

    public void setReceivedDonations(List<Donation> receivedDonations) {
        this.receivedDonations = receivedDonations;
    }

    // UserDetails Implementation (Updated to use role)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked;  // Locked if blocked
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}