package com.myhome.service;

import com.myhome.dao.UserDAO;
import com.myhome.model.User;
import com.myhome.util.PasswordUtil;

import java.util.Optional;

/**
 * AuthService
 * -------
 * Registration and login rules live here, on top of UserDAO. Passwords are
 * always hashed (see PasswordUtil) before they reach the CSV file.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public enum RegisterResult { SUCCESS, DUPLICATE, INVALID }

    public RegisterResult registerResident(String fullName, String email, String phone,
                                            String password, String flatNumber) {
        if (fullName.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            return RegisterResult.INVALID;
        }
        if (!email.contains("@") || !email.contains(".")) {
            return RegisterResult.INVALID;
        }
        if (password.length() < 4) {
            return RegisterResult.INVALID;
        }
        if (userDAO.existsByEmailOrPhone(email, phone)) {
            return RegisterResult.DUPLICATE;
        }

        User user = new User(
                userDAO.nextId(), fullName, email, phone,
                PasswordUtil.hash(password), "RESIDENT", flatNumber, false
        );
        userDAO.save(user);
        return RegisterResult.SUCCESS;
    }

    /** Seeds a default admin account the first time the app runs (email: admin@myhome.com / pass: admin123). */
    public void ensureDefaultAdmin() {
        if (userDAO.findByRole("ADMIN").isEmpty()) {
            User admin = new User(
                    userDAO.nextId(), "System Admin", "admin@myhome.com", "0000000000",
                    PasswordUtil.hash("admin123"), "ADMIN", "", true
            );
            userDAO.save(admin);
        }
    }

    public Optional<User> login(String identifier, String password, String expectedRole) {
        Optional<User> userOpt = userDAO.findByEmailOrPhone(identifier);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }
        User user = userOpt.get();
        if (!user.getRole().equalsIgnoreCase(expectedRole)) {
            return Optional.empty();
        }
        if (!PasswordUtil.matches(password, user.getPasswordHash())) {
            return Optional.empty();
        }
        return Optional.of(user);
    }
}
