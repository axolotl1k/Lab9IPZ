package com.example.IPZ_Lab9.controller;

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

/**
 * Controller responsible for handling user authentication and registration.
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
     * @return the "register" view.
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    /**
     * Handles user registration.
     *
     * @param username the username provided by the user.
     * @param password the password provided by the user.
     * @param session  the current HTTP session to store the registered user.
     * @param model    the model to pass data to the view.
     * @return a redirect to the account creation page if successful, or back to the registration page with an error message.
     */
    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            User user = userService.register(username, password);
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
     * @return the "login" view.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Handles user login.
     *
     * @param username the username provided by the user.
     * @param password the password provided by the user.
     * @param session  the current HTTP session to store the authenticated user.
     * @param model    the model to pass data to the view.
     * @return a redirect to the account page if successful, or back to the login page with an error message.
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            User user = userService.authenticate(username, password);
            session.setAttribute("currentUser", user);

            if (accountService.getAccountsByOwner(user.getUsername()).isEmpty()) {
                return "redirect:/accounts/create";
            }
            return "redirect:/account";
        } catch (Exception e) {
            model.addAttribute("message", e.getMessage());
            return "login";
        }
    }

    /**
     * Logs out the current user by invalidating the session.
     *
     * @param session the current HTTP session to invalidate.
     * @return a redirect to the login page.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
