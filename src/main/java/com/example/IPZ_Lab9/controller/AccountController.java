package com.example.IPZ_Lab9.controller;

import com.example.IPZ_Lab9.model.User;
import com.example.IPZ_Lab9.service.AccountService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    /**
     * Displays the account view page, including account details and list of accounts.
     *
     * @param session   the current HTTP session to retrieve the logged-in user.
     * @param accountId the ID of the account to display (optional).
     * @param model     the model to pass data to the view.
     * @return the "account" view.
     */
    @GetMapping("/account")
    public String viewAccount(HttpSession session, @RequestParam(required = false) Long accountId, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAllAttributes(accountService.prepareAccountView(currentUser.getUsername(), accountId));

        // Transfer the message from the session to the model
        String message = (String) session.getAttribute("message");
        if (message != null) {
            model.addAttribute("message", message);
            session.removeAttribute("message"); // Remove the message from the session
        }

        return "account";
    }

    /**
     * Displays the page for creating a new account.
     *
     * @return the "create-account" view.
     */
    @GetMapping("/accounts/create")
    public String showCreateAccountPage() {
        return "create-account";
    }

    /**
     * Handles the creation of a new account for the logged-in user.
     *
     * @param session       the current HTTP session to retrieve the logged-in user.
     * @param currency      the currency of the new account.
     * @param initialBalance the initial balance of the new account.
     * @return a redirect to the "account" view.
     */
    @PostMapping("/accounts/create")
    public String createAccount(HttpSession session, @RequestParam String currency,
                                @RequestParam Double initialBalance) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        accountService.createNewAccount(currentUser.getUsername(), currency, initialBalance);
        return "redirect:/account";
    }

    /**
     * Handles depositing money into a specific account.
     *
     * @param accountId the ID of the account to deposit into.
     * @param amount    the amount to deposit.
     * @param session   the current HTTP session to store messages.
     * @return a redirect to the "account" view.
     */
    @PostMapping("/account/deposit")
    public String deposit(@RequestParam Long accountId, @RequestParam Double amount, HttpSession session) {
        try {
            accountService.deposit(accountId, amount);
            session.setAttribute("message", "Deposit successful");
        } catch (Exception e) {
            session.setAttribute("message", "Deposit failed: " + e.getMessage());
        }
        return "redirect:/account";
    }

    /**
     * Handles transferring money between accounts.
     *
     * @param session       the current HTTP session to retrieve the logged-in user.
     * @param fromAccountId the ID of the source account.
     * @param toAccountId   the ID of the target account.
     * @param amount        the amount to transfer.
     * @return a redirect to the "account" view of the source account.
     */
    @PostMapping("/accounts/transfer")
    public String transferMoney(HttpSession session,
                                @RequestParam Long fromAccountId,
                                @RequestParam Long toAccountId,
                                @RequestParam Double amount) {
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
     * @param session   the current HTTP session to retrieve the logged-in user.
     * @param accountId the ID of the account to delete.
     * @return a redirect to the "account" view.
     */
    @PostMapping("/accounts/delete")
    public String deleteAccount(HttpSession session, @RequestParam Long accountId) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            accountService.deleteAccount(accountId, currentUser.getUsername());
            session.setAttribute("message", "Account deleted successfully");
        } catch (Exception e) {
            session.setAttribute("message", "Failed to delete account: " + e.getMessage());
        }

        return "redirect:/account";
    }
}
