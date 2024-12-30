package com.example.IPZ_Lab9.repository;

import com.example.IPZ_Lab9.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Provides methods for performing database operations on users.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    Optional<User> findByUsername(String username);
}
