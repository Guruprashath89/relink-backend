package com.example.user_auth_api.repository;

import com.example.user_auth_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);  // For loadUserByUsername

    boolean existsByUsername(String username);  // For registration check

    boolean existsByEmail(String email);  // For registration check
}