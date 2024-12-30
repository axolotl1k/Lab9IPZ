package com.example.IPZ_Lab9.service;

import com.example.IPZ_Lab9.model.Account;
import com.example.IPZ_Lab9.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for business logic related to accounts.
 * Provides methods for managing accounts, such as creating, retrieving, updating, and deleting them.
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Retrieves all accounts owned by a specific user.
     *
     * @param owner the username of the account owner
     * @return a list of accounts owned by the specified user
     */
    public List<Account> getAccountsByOwner(String owner) {
        return accountRepository.findByOwner(owner);
    }

    /**
     * Creates a new account.
     *
     * @param account the account to create
     * @return the created account
     */
    public Account createAccount(Account account) {
        return accountRepository.save(account);
    }

    /**
     * Deposits a specified amount into an account.
     *
     * @param accountId the ID of the account to deposit into
     * @param amount    the amount to deposit
     */
    @Transactional
    public void deposit(Long accountId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }

    /**
     * Transfers money between accounts.
     *
     * @param fromAccountId the ID of the source account
     * @param toAccountId   the ID of the target account
     * @param amount        the amount to transfer
     * @param owner         the username of the account owner initiating the transfer
     */
    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, Double amount, String owner) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("You cannot transfer money to the same account");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new IllegalArgumentException("Target account not found"));

        // Check ownership of the source account
        if (!fromAccount.getOwner().equals(owner)) {
            throw new SecurityException("You are not authorized to transfer from this account");
        }

        // Check if currencies match
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new IllegalArgumentException("Transfer failed: Currencies of both accounts must match");
        }

        // Check if balance is sufficient
        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Perform the transfer
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        // Save changes
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    /**
     * Deletes an account owned by a specific user.
     *
     * @param accountId the ID of the account to delete
     * @param owner     the username of the account owner
     */
    @Transactional
    public void deleteAccount(Long accountId, String owner) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (!account.getOwner().equals(owner)) {
            throw new SecurityException("You are not authorized to delete this account");
        }

        accountRepository.delete(account);
    }

    /**
     * Retrieves an account by its ID.
     *
     * @param accountId the ID of the account to retrieve
     * @return an Optional containing the account if found, or empty if not found
     */
    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }
}
