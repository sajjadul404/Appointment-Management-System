package com.nestora.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * PasswordUtil
 * ------------
 * Hashes passwords with SHA-256 before they ever touch a CSV file, so plain
 * text passwords are never stored. (For a real production system you'd want
 * a salted, slow hash like BCrypt/Argon2 -- SHA-256 here keeps the project
 * dependency-free while still meeting the "never store plain text
 * passwords" requirement.)
 */
public final class PasswordUtil {

    private PasswordUtil() {
    }

    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public static boolean matches(String rawPassword, String hashedPassword) {
        return hash(rawPassword).equals(hashedPassword);
    }
}
