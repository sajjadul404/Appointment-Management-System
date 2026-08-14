package com.nestora.dao;

import com.nestora.model.User;
import com.nestora.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * UserDAO
 * -------
 * All persistence for residents and admins lives in data/users.csv.
 * Columns: id|fullName|email|phone|passwordHash|role|flatNumber|verified
 */
public class UserDAO {

    private static final String PATH = "data/users.csv";
    private static final String HEADER = "id|fullName|email|phone|passwordHash|role|flatNumber|verified";

    public UserDAO() {
        CsvUtil.ensureFile(PATH, HEADER);
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        for (String[] cols : CsvUtil.readRows(PATH)) {
            users.add(User.fromCsvRow(cols));
        }
        return users;
    }

    public List<User> findByRole(String role) {
        List<User> result = new ArrayList<>();
        for (User u : findAll()) {
            if (u.getRole().equalsIgnoreCase(role)) {
                result.add(u);
            }
        }
        return result;
    }

    public Optional<User> findByEmailOrPhone(String identifier) {
        for (User u : findAll()) {
            if (u.getEmail().equalsIgnoreCase(identifier) || u.getPhone().equals(identifier)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public boolean existsByEmailOrPhone(String email, String phone) {
        for (User u : findAll()) {
            if (u.getEmail().equalsIgnoreCase(email) || u.getPhone().equals(phone)) {
                return true;
            }
        }
        return false;
    }

    public User save(User user) {
        CsvUtil.appendRow(PATH, user.toCsvRow());
        return user;
    }

    /** Rewrites the whole file with the updated user list (used for edits: verify, change password, etc.). */
    public void updateAll(List<User> users) {
        List<String> rows = new ArrayList<>();
        for (User u : users) {
            rows.add(u.toCsvRow());
        }
        CsvUtil.writeAll(PATH, HEADER, rows);
    }

    public int nextId() {
        return CsvUtil.nextId(PATH);
    }
}
