package com.example.IPZ_Lab9.controller;

import com.example.IPZ_Lab9.model.Account;
import com.example.IPZ_Lab9.model.User;
import com.example.IPZ_Lab9.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller responsible for handling account-related actions,
 * such as viewing accounts, creating new accounts,
 * performing transfers, and deleting accounts.
 */
@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Displays the account page for the current user.
     * If an account ID is provided, it shows details of that specific account.
     * Otherwise, it defaults to showing the first account.
     *
     * @param session the HTTP session to get the current user
     * @param accountId the ID of the account to view (optional)
     * @param model the model to pass data to the view
     * @return the account view template
     */
    @GetMapping("/account")
    public String viewAccount(HttpSession session, @RequestParam(required = false) Long accountId, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        List<Account> accounts = accountService.getAccountsByOwner(currentUser.getUsername());
        model.addAttribute("accounts", accounts);

        if (accountId == null && !accounts.isEmpty()) {
            model.addAttribute("account", accounts.get(0));
        } else if (accountId != null) {
            accounts.stream()
                    .filter(account -> account.getId().equals(accountId))
                    .findFirst()
                    .ifPresent(account -> model.addAttribute("account", account));
        }

        String message = (String) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message");
        }

        return "account";
    }

    /**
     * Displays the page for creating a new account.
     *
     * @return the create-account view template
     */
    @GetMapping("/accounts/create")
    public String showCreateAccountPage() {
        return "create-account";
    }

    /**
     * Handles the creation of a new account for the current user.
     *
     * @param session the HTTP session to get the current user
     * @param currency the currency of the new account
     * @param initialBalance the initial balance of the new account
     * @param model the model to pass data to the view
     * @return redirect to the account page
     */
    @PostMapping("/accounts/create")
    public String createAccount(HttpSession session, @RequestParam String currency,
                                @RequestParam Double initialBalance, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Account account = new Account();
        account.setOwner(currentUser.getUsername().toLowerCase());
        account.setCurrency(currency);
        account.setBalance(initialBalance);

        accountService.createAccount(account);
        return "redirect:/account";
    }

    /**
     * Handles depositing money into an account.
     *
     * @param accountId the ID of the account to deposit into
     * @param amount the amount to deposit
     * @param model the model to pass data to the view
     * @return redirect to the account page
     */
    @PostMapping("/account/deposit")
    public String deposit(@RequestParam Long accountId, @RequestParam Double amount, Model model) {
        try {
            accountService.deposit(accountId, amount);
            model.addAttribute("message", "Deposit successful");
        } catch (Exception e) {
            model.addAttribute("message", "Deposit failed: " + e.getMessage());
        }
        return "redirect:/account";
    }

    /**
     * Handles transferring money between accounts.
     *
     * @param session the HTTP session to get the current user
     * @param fromAccountId the ID of the account to transfer from
     * @param toAccountId the ID of the account to transfer to
     * @param amount the amount to transfer
     * @param model the model to pass data to the view
     * @return redirect to the account page of the source account
     */
    @PostMapping("/accounts/transfer")
    public String transferMoney(HttpSession session,
                                @RequestParam Long fromAccountId,
                                @RequestParam Long toAccountId,
                                @RequestParam Double amount,
                                Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            accountService.transfer(fromAccountId, toAccountId, amount, currentUser.getUsername());
            session.setAttribute("message", "Transfer successful");
        } catch (IllegalArgumentException e) {
            session.setAttribute("message", e.getMessage());
        } catch (Exception e) {
            session.setAttribute("message", "An unexpected error occurred: " + e.getMessage());
        }

        return "redirect:/account?accountId=" + fromAccountId;
    }

    /**
     * Handles deleting an account.
     *
     * @param session the HTTP session to get the current user
     * @param accountId the ID of the account to delete
     * @param model the model to pass data to the view
     * @return redirect to the account page
     */
    @PostMapping("/accounts/delete")
    public String deleteAccount(HttpSession session, @RequestParam Long accountId, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            accountService.deleteAccount(accountId, currentUser.getUsername());
            model.addAttribute("message", "Account deleted successfully");
        } catch (Exception e) {
            model.addAttribute("message", "Failed to delete account: " + e.getMessage());
        }

        return "redirect:/account";
    }
}
