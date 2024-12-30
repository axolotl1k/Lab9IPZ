package com.example.IPZ_Lab9.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a user in the system.
 * Contains user-specific information such as username and password.
 */
@Entity
@Data
@Table(name = "users") // Specifies the custom table name for the User entity
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Unique username of the user.
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * Password of the user.
     */
    @Column(nullable = false)
    private String password;
}
