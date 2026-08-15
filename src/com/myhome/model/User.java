package com.myhome.model;

/**
 * User
 * ----
 * Represents both Residents and Admins. The `role` field decides which
 * menu the person gets after login. Residents additionally carry a flat
 * number and a verification flag (set by an Admin).
 */
public class User {

    private int id;
    private String fullName;
    private String email;
    private String phone;
    private String passwordHash;
    private String role;        // "RESIDENT" or "ADMIN"
    private String flatNumber;  // empty for admins
    private boolean verified;   // residents start unverified until admin approves

    public User(int id, String fullName, String email, String phone, String passwordHash,
                String role, String flatNumber, boolean verified) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.role = role;
        this.flatNumber = flatNumber;
        this.verified = verified;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPasswordHash() { return passwordHash; }
    public String getRole() { return role; }
    public String getFlatNumber() { return flatNumber; }
    public boolean isVerified() { return verified; }

    public void setVerified(boolean verified) { this.verified = verified; }
    public void setFlatNumber(String flatNumber) { this.flatNumber = flatNumber; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /** Serializes this user as one pipe-delimited CSV row (see CsvUtil for the format rules). */
    public String toCsvRow() {
        return id + "|" + fullName + "|" + email + "|" + phone + "|" + passwordHash + "|"
                + role + "|" + flatNumber + "|" + (verified ? "Y" : "N");
    }

    public static User fromCsvRow(String[] cols) {
        return new User(
                Integer.parseInt(cols[0]),
                cols[1],
                cols[2],
                cols[3],
                cols[4],
                cols[5],
                cols[6],
                "Y".equals(cols[7])
        );
    }
}
