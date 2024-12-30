package com.example.IPZ_Lab9.repository;

import com.example.IPZ_Lab9.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for managing Account entities.
 * Provides methods for performing database operations on accounts.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds a list of accounts owned by a specific user.
     *
     * @param owner the username of the account owner
     * @return a list of accounts belonging to the specified owner
     */
    List<Account> findByOwner(String owner);
}
