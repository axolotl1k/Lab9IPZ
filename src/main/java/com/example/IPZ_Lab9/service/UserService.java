package com.example.IPZ_Lab9.service;

import com.example.IPZ_Lab9.model.User;
import com.example.IPZ_Lab9.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service responsible for managing user operations such as registration and authentication.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Finds a user by their username.
     *
     * @param username the username to search.
     * @return an Optional containing the User, or empty if not found.
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username.toLowerCase());
    }

    /**
     * Registers a new user.
     *
     * @param username the username for the new user.
     * @param password the password for the new user.
     * @return the newly registered User.
     * @throws IllegalArgumentException if the username is already taken or if input is invalid.
     */
    public User register(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        String normalizedUsername = username.toLowerCase();

        if (userRepository.findByUsername(normalizedUsername).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(normalizedUsername);
        user.setPassword(password);
        return userRepository.save(user);
    }

    /**
     * Authenticates a user with the provided credentials.
     *
     * @param username the username to authenticate.
     * @param password the password to authenticate.
     * @return the authenticated User.
     * @throws IllegalArgumentException if the credentials are invalid.
     */
    public User authenticate(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty");
        }

        String normalizedUsername = username.toLowerCase();

        return userRepository.findByUsername(normalizedUsername)
                .filter(user -> user.getPassword().equals(password))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
    }
}
