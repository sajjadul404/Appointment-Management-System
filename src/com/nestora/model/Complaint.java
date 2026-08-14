package com.nestora.model;

/**
 * Complaint
 * ---------
 * A Help Desk ticket raised by a resident.
 */
public class Complaint {

    private int id;
    private String residentEmail;
    private String category;   // Water, Electricity, Lift, Security, Cleaning, Maintenance, Parking, Other
    private String subject;
    private String description;
    private String status;     // PENDING, IN_PROGRESS, RESOLVED, REJECTED
    private String date;

    public Complaint(int id, String residentEmail, String category, String subject,
                      String description, String status, String date) {
        this.id = id;
        this.residentEmail = residentEmail;
        this.category = category;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.date = date;
    }

    public int getId() { return id; }
    public String getResidentEmail() { return residentEmail; }
    public String getCategory() { return category; }
    public String getSubject() { return subject; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getDate() { return date; }

    public void setStatus(String status) { this.status = status; }

    public String toCsvRow() {
        return id + "|" + residentEmail + "|" + category + "|" + subject + "|"
                + description + "|" + status + "|" + date;
    }

    public static Complaint fromCsvRow(String[] cols) {
        return new Complaint(
                Integer.parseInt(cols[0]),
                cols[1], cols[2], cols[3], cols[4], cols[5], cols[6]
        );
    }
}
