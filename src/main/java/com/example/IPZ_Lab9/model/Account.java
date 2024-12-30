package com.example.IPZ_Lab9.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Represents a user's account in the system.
 * Contains information about the account balance, currency, and the owner.
 */
@Entity
@Data
public class Account {

    /**
     * Unique identifier for the account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Current balance of the account.
     */
    @Column(nullable = false)
    private Double balance;

    /**
     * Currency type of the account (e.g., USD, EUR).
     */
    @Column(nullable = false)
    private String currency;

    /**
     * Username of the account owner.
     */
    @Column(nullable = false)
    private String owner;
}
