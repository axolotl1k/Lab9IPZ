package com.example.IPZ_Lab9.service;

import com.example.IPZ_Lab9.model.Account;
import com.example.IPZ_Lab9.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service layer for managing account-related operations.
 * Handles business logic for creating, retrieving, updating, and deleting accounts.
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    /**
     * Prepares data for displaying account details and the list of accounts.
     *
     * @param currentUser the currently logged-in user.
     * @param accountId   the ID of the account to display (optional).
     * @return a map of attributes to be passed to the view.
     */
    public Map<String, Object> prepareAccountView(String currentUser, Long accountId) {
        List<Account> accounts = getAccountsByOwner(currentUser);
        Map<String, Object> modelData = new HashMap<>();

        modelData.put("accounts", accounts);

        if (accountId == null && !accounts.isEmpty()) {
            modelData.put("account", accounts.get(0));
        } else if (accountId != null) {
            accounts.stream()
                    .filter(account -> account.getId().equals(accountId))
                    .findFirst()
                    .ifPresent(account -> modelData.put("account", account));
        }

        return modelData;
    }

    /**
     * Retrieves a list of accounts owned by the specified user.
     *
     * @param owner the username of the account owner.
     * @return a list of accounts owned by the user.
     */
    public List<Account> getAccountsByOwner(String owner) {
        return accountRepository.findByOwner(owner);
    }

    /**
     * Creates a new account with the specified parameters.
     *
     * @param owner         the username of the account owner.
     * @param currency      the currency of the account.
     * @param initialBalance the initial balance of the account.
     */
    public void createNewAccount(String owner, String currency, Double initialBalance) {
        Account account = new Account();
        account.setOwner(owner.toLowerCase());
        account.setCurrency(currency);
        account.setBalance(initialBalance);

        accountRepository.save(account);
    }

    /**
     * Deposits a specified amount into the account.
     *
     * @param accountId the ID of the account to deposit into.
     * @param amount    the amount to deposit.
     * @throws IllegalArgumentException if the amount is invalid or the account is not found.
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
     * Transfers money from one account to another.
     *
     * @param fromAccountId the ID of the source account.
     * @param toAccountId   the ID of the target account.
     * @param amount        the amount to transfer.
     * @param owner         the username of the account owner initiating the transfer.
     * @throws IllegalArgumentException if the transfer fails due to validation issues.
     * @throws SecurityException        if the user is not authorized to perform the transfer.
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

        // Check for ownership
        if (!fromAccount.getOwner().equals(owner)) {
            throw new SecurityException("You are not authorized to transfer from this account");
        }

        // Check for matching currencies
        if (!fromAccount.getCurrency().equals(toAccount.getCurrency())) {
            throw new IllegalArgumentException("Transfer failed: Currencies of both accounts must match");
        }

        // Check for sufficient balance
        if (fromAccount.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        // Perform transfer
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);

        // Save changes
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
    }

    /**
     * Deletes an account if the user is authorized.
     *
     * @param accountId the ID of the account to delete.
     * @param owner     the username of the account owner.
     * @throws SecurityException if the user is not authorized to delete the account.
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
}
