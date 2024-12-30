package com.example.IPZ_Lab9.service;

import com.example.IPZ_Lab9.model.User;
import com.example.IPZ_Lab9.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for business logic related to users.
 * Provides methods for user registration and retrieval by username.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Finds a user by their unique username.
     *
     * @param username the username of the user
     * @return an Optional containing the user if found, or empty if not found
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Registers a new user in the system.
     *
     * @param user the user to register
     * @return the registered user
     * @throws IllegalArgumentException if a user with the same username already exists
     */
    public User register(User user) {
        user.setUsername(user.getUsername().toLowerCase());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        return userRepository.save(user);
    }
}
