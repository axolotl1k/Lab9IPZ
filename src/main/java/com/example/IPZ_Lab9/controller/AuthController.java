package com.example.IPZ_Lab9.controller;

import com.example.IPZ_Lab9.model.Account;
import com.example.IPZ_Lab9.model.User;
import com.example.IPZ_Lab9.service.AccountService;
import com.example.IPZ_Lab9.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

/**
 * Controller responsible for user authentication, including registration, login, and logout.
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    /**
     * Displays the registration page.
     *
     * @return the registration view template
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * Handles the registration of a new user.
     *
     * @param username the username of the new user
     * @param password the password of the new user
     * @param session  the HTTP session to store the current user
     * @param model    the model to pass data to the view
     * @return redirect to the account creation page or return to the registration page with an error message
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            userService.register(user);
            session.setAttribute("currentUser", user);
            return "redirect:/accounts/create";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "register";
        }
    }

    /**
     * Displays the login page.
     *
     * @return the login view template
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Handles user login.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @param session  the HTTP session to store the current user
     * @param model    the model to pass data to the view
     * @return redirect to the account page if successful or return to the login page with an error message
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            model.addAttribute("message", "Username and password cannot be empty");
            return "login";
        }

        // Convert the entered username to lowercase for consistency
        Optional<User> user = userService.findByUsername(username.toLowerCase());
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            session.setAttribute("currentUser", user.get());

            List<Account> accounts = accountService.getAccountsByOwner(username.toLowerCase());

            if (!accounts.isEmpty()) {
                return "redirect:/account"; // Redirect to the account page if accounts exist
            } else {
                return "redirect:/accounts/create"; // Redirect to account creation page if no accounts exist
            }
        }

        model.addAttribute("message", "Invalid username or password");
        return "login"; // Return to login page if credentials are invalid
    }

    /**
     * Handles user logout by invalidating the current session.
     *
     * @param session the HTTP session to invalidate
     * @return redirect to the login page
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clear the session
        return "redirect:/login"; // Redirect to the login page
    }
}
