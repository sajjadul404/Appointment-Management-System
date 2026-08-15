package com.myhome.model;

/**
 * Payment
 * -------
 * A simulated payment record (rent, utility, or maintenance). No real
 * payment gateway is used -- this just records what the resident "paid".
 */
public class Payment {

    private int id;
    private String residentEmail;
    private String type;       // RENT, WATER, GAS, ELECTRICITY, LIFT, MAINTENANCE
    private double amount;
    private String method;     // CASH, BANK_TRANSFER, MOBILE_BANKING, CARD
    private String status;     // PAID, PARTIAL, DUE, OVERDUE
    private String date;
    private String transactionId;

    public Payment(int id, String residentEmail, String type, double amount, String method,
                    String status, String date, String transactionId) {
        this.id = id;
        this.residentEmail = residentEmail;
        this.type = type;
        this.amount = amount;
        this.method = method;
        this.status = status;
        this.date = date;
        this.transactionId = transactionId;
    }

    public int getId() { return id; }
    public String getResidentEmail() { return residentEmail; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public String getMethod() { return method; }
    public String getStatus() { return status; }
    public String getDate() { return date; }
    public String getTransactionId() { return transactionId; }

    public String toCsvRow() {
        return id + "|" + residentEmail + "|" + type + "|" + amount + "|" + method + "|"
                + status + "|" + date + "|" + transactionId;
    }

    public static Payment fromCsvRow(String[] cols) {
        return new Payment(
                Integer.parseInt(cols[0]),
                cols[1], cols[2],
                Double.parseDouble(cols[3]),
                cols[4], cols[5], cols[6], cols[7]
        );
    }
}
