package com.myhome;

import com.myhome.dao.*;
import com.myhome.model.*;
import com.myhome.service.AuthService;
import com.myhome.util.ConsoleUtil;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Main
 * MY HOME - Apartment Management System
 * Default admin login: admin@myhome.com / admin123
 */
public class Main {

    private static final UserDAO userDAO = new UserDAO();
    private static final ApartmentDAO apartmentDAO = new ApartmentDAO();
    private static final ComplaintDAO complaintDAO = new ComplaintDAO();
    private static final NoticeDAO noticeDAO = new NoticeDAO();
    private static final PaymentDAO paymentDAO = new PaymentDAO();
    private static final AuthService authService = new AuthService(userDAO);

    public static void main(String[] args) {
        authService.ensureDefaultAdmin();
        ConsoleUtil.printBanner();
        mainMenu();
    }

    // =================================================================
    // MAIN MENU
    // =================================================================
    private static void mainMenu() {
        while (true) {
            ConsoleUtil.printMenu("Welcome to MY HOME", List.of(
                    "Register as Resident",
                    "Login as Resident",
                    "Login as Admin",
                    "Exit"
            ));
            int choice = ConsoleUtil.readMenuChoice(4);
            switch (choice) {
                case 1 -> safe(Main::registerResident);
                case 2 -> safe(() -> loginFlow("RESIDENT"));
                case 3 -> safe(() -> loginFlow("ADMIN"));
                case 4 -> {
                    ConsoleUtil.printInfo("Thank you for using MY HOME. Goodbye!");
                    return;
                }
            }
        }
    }

    private static void registerResident() {
        ConsoleUtil.printHeader("Resident Registration");
        String fullName = ConsoleUtil.readRequired("Full Name");
        String email = ConsoleUtil.readRequired("Email");
        String phone = ConsoleUtil.readRequired("Phone");
        String flat = ConsoleUtil.readRequired("Flat Number (e.g. B1-3F-302)");
        String password = ConsoleUtil.readPassword("Password (min 4 characters)");
        String confirm = ConsoleUtil.readPassword("Confirm Password");

        if (!password.equals(confirm)) {
            ConsoleUtil.printError("Passwords do not match.");
            ConsoleUtil.pause();
            return;
        }

        AuthService.RegisterResult result = authService.registerResident(fullName, email, phone, password, flat);
        switch (result) {
            case SUCCESS -> ConsoleUtil.printSuccess(
                    "Registration submitted successfully. Waiting for admin verification.");
            case DUPLICATE -> ConsoleUtil.printError("An account with this email or phone already exists.");
            case INVALID -> ConsoleUtil.printError(
                    "Please check your details (valid email, password 4+ characters, no empty fields).");
        }
        ConsoleUtil.pause();
    }

    private static void loginFlow(String role) {
        ConsoleUtil.printHeader((role.equals("ADMIN") ? "Admin" : "Resident") + " Login");
        String identifier = ConsoleUtil.readRequired("Email / Phone");
        String password = ConsoleUtil.readPassword("Password");

        Optional<User> userOpt = authService.login(identifier, password, role);
        if (userOpt.isEmpty()) {
            ConsoleUtil.printError("Invalid credentials or account not found.");
            ConsoleUtil.pause();
            return;
        }

        User user = userOpt.get();
        if (role.equals("RESIDENT") && !user.isVerified()) {
            ConsoleUtil.printInfo("Your account is still pending admin verification. Please check back later.");
            ConsoleUtil.pause();
            return;
        }

        ConsoleUtil.printSuccess("Welcome, " + user.getFullName() + "!");
        ConsoleUtil.pause();

        if (role.equals("ADMIN")) {
            adminMenu(user);
        } else {
            residentMenu(user);
        }
    }

    // =================================================================
    // RESIDENT AREA
    // =================================================================
    private static void residentMenu(User resident) {
        while (true) {
            ConsoleUtil.printMenu("Resident Dashboard - " + resident.getFullName(), List.of(
                    "View Profile",
                    "My Apartment",
                    "Submit Complaint",
                    "My Complaints",
                    "View Notices",
                    "Make a Payment",
                    "Payment History",
                    "Emergency Contacts",
                    "Logout"
            ));
            int choice = ConsoleUtil.readMenuChoice(9);
            switch (choice) {
                case 1 -> safe(() -> viewProfile(resident));
                case 2 -> safe(() -> viewMyApartment(resident));
                case 3 -> safe(() -> submitComplaint(resident));
                case 4 -> safe(() -> viewMyComplaints(resident));
                case 5 -> safe(Main::viewNotices);
                case 6 -> safe(() -> makePayment(resident));
                case 7 -> safe(() -> viewPaymentHistory(resident));
                case 8 -> safe(Main::showEmergencyContacts);
                case 9 -> { return; }
            }
        }
    }

    private static void viewProfile(User resident) {
        ConsoleUtil.printSubHeader("My Profile");
        ConsoleUtil.printRow("Name:", resident.getFullName());
        ConsoleUtil.printRow("Email:", resident.getEmail());
        ConsoleUtil.printRow("Phone:", resident.getPhone());
        ConsoleUtil.printRow("Flat Number:", resident.getFlatNumber());
        ConsoleUtil.printRow("Verification:", resident.isVerified() ? "Verified" : "Pending");
        ConsoleUtil.pause();
    }

    private static void viewMyApartment(User resident) {
        ConsoleUtil.printSubHeader("My Apartment");
        Optional<Apartment> match = apartmentDAO.findAll().stream()
                .filter(a -> a.getFlatNumber().equalsIgnoreCase(resident.getFlatNumber()))
                .findFirst();

        if (match.isEmpty()) {
            ConsoleUtil.printInfo("No apartment record found yet for flat " + resident.getFlatNumber()
                    + ". Ask the admin to add it under Apartment Management.");
        } else {
            Apartment a = match.get();
            ConsoleUtil.printRow("Building:", a.getBuilding());
            ConsoleUtil.printRow("Block:", a.getBlock());
            ConsoleUtil.printRow("Floor:", a.getFloor());
            ConsoleUtil.printRow("Flat Number:", a.getFlatNumber());
            ConsoleUtil.printRow("Size:", a.getSize());
            ConsoleUtil.printRow("Status:", a.getStatus());
        }
        ConsoleUtil.pause();
    }

    private static void submitComplaint(User resident) {
        ConsoleUtil.printSubHeader("Submit a Complaint");
        ConsoleUtil.printInfo("Categories: Water, Electricity, Lift, Security, Cleaning, Maintenance, Parking, Other");
        String category = ConsoleUtil.readRequired("Category");
        String subject = ConsoleUtil.readRequired("Subject");
        String description = ConsoleUtil.readRequired("Description");

        Complaint complaint = new Complaint(
                complaintDAO.nextId(), resident.getEmail(), category, subject,
                description, "PENDING", today()
        );
        complaintDAO.save(complaint);
        ConsoleUtil.printSuccess("Complaint #" + complaint.getId() + " submitted. Status: PENDING");
        ConsoleUtil.pause();
    }

    private static void viewMyComplaints(User resident) {
        ConsoleUtil.printSubHeader("My Complaints");
        List<Complaint> complaints = complaintDAO.findByResident(resident.getEmail());
        if (complaints.isEmpty()) {
            ConsoleUtil.printInfo("You haven't submitted any complaints yet.");
        } else {
            ConsoleUtil.printTableHeader("ID", "Category", "Subject", "Status", "Date");
            for (Complaint c : complaints) {
                ConsoleUtil.printRow(String.valueOf(c.getId()), c.getCategory(), c.getSubject(), c.getStatus(), c.getDate());
            }
        }
        ConsoleUtil.pause();
    }

    private static void viewNotices() {
        ConsoleUtil.printSubHeader("Notice Board");
        List<Notice> notices = noticeDAO.findAll();
        if (notices.isEmpty()) {
            ConsoleUtil.printInfo("No notices published yet.");
        } else {
            for (Notice n : notices) {
                System.out.println();
                ConsoleUtil.printRow("[" + n.getType() + "]", n.getTitle(), n.getDate());
                System.out.println("      " + n.getDescription());
            }
        }
        ConsoleUtil.pause();
    }

    private static void makePayment(User resident) {
        ConsoleUtil.printSubHeader("Make a Payment (Simulated)");
        ConsoleUtil.printInfo("Types: RENT, WATER, GAS, ELECTRICITY, LIFT, MAINTENANCE");
        String type = ConsoleUtil.readRequired("Payment Type");
        double amount = ConsoleUtil.readDouble("Amount");
        ConsoleUtil.printInfo("Methods: CASH, BANK_TRANSFER, MOBILE_BANKING, CARD");
        String method = ConsoleUtil.readRequired("Payment Method");

        String transactionId = "TXN" + System.currentTimeMillis();
        Payment payment = new Payment(
                paymentDAO.nextId(), resident.getEmail(), type.toUpperCase(), amount,
                method.toUpperCase(), "PAID", today(), transactionId
        );
        paymentDAO.save(payment);

        ConsoleUtil.printSuccess("Payment successful!");
        ConsoleUtil.printRow("Transaction ID:", transactionId);
        ConsoleUtil.printRow("Amount:", String.format("%.2f", amount));
        ConsoleUtil.printRow("Status:", "PAID");
        ConsoleUtil.pause();
    }

    private static void viewPaymentHistory(User resident) {
        ConsoleUtil.printSubHeader("Payment History");
        List<Payment> payments = paymentDAO.findByResident(resident.getEmail());
        if (payments.isEmpty()) {
            ConsoleUtil.printInfo("No payments recorded yet.");
        } else {
            ConsoleUtil.printTableHeader("ID", "Type", "Amount", "Method", "Status", "Date");
            for (Payment p : payments) {
                ConsoleUtil.printRow(String.valueOf(p.getId()), p.getType(),
                        String.format("%.2f", p.getAmount()), p.getMethod(), p.getStatus(), p.getDate());
            }
        }
        ConsoleUtil.pause();
    }

    private static void showEmergencyContacts() {
        ConsoleUtil.printSubHeader("Emergency Contacts");
        ConsoleUtil.printRow("Ambulance:", "999");
        ConsoleUtil.printRow("Fire Brigade:", "999");
        ConsoleUtil.printRow("Police:", "999");
        ConsoleUtil.printRow("Building Security:", "01700-000000");
        ConsoleUtil.pause();
    }

    // =================================================================
    // ADMIN AREA
    // =================================================================
    private static void adminMenu(User admin) {
        while (true) {
            ConsoleUtil.printMenu("Admin Dashboard - " + admin.getFullName(), List.of(
                    "Dashboard Summary",
                    "View All Residents",
                    "Verify a Resident",
                    "Apartment Management",
                    "View All Complaints",
                    "Update Complaint Status",
                    "Post a Notice",
                    "View All Payments",
                    "Logout"
            ));
            int choice = ConsoleUtil.readMenuChoice(9);
            switch (choice) {
                case 1 -> safe(Main::dashboardSummary);
                case 2 -> safe(Main::viewAllResidents);
                case 3 -> safe(Main::verifyResident);
                case 4 -> safe(Main::apartmentManagement);
                case 5 -> safe(Main::viewAllComplaints);
                case 6 -> safe(Main::updateComplaintStatus);
                case 7 -> safe(() -> postNotice(admin));
                case 8 -> safe(Main::viewAllPayments);
                case 9 -> { return; }
            }
        }
    }

    private static void dashboardSummary() {
        ConsoleUtil.printSubHeader("Dashboard Summary");
        List<User> residents = userDAO.findByRole("RESIDENT");
        long verified = residents.stream().filter(User::isVerified).count();
        long pendingVerification = residents.size() - verified;
        List<Apartment> apartments = apartmentDAO.findAll();
        long occupied = apartments.stream().filter(a -> a.getStatus().equalsIgnoreCase("OCCUPIED")).count();
        long vacant = apartments.stream().filter(a -> a.getStatus().equalsIgnoreCase("VACANT")).count();
        List<Complaint> complaints = complaintDAO.findAll();
        long pendingComplaints = complaints.stream().filter(c -> c.getStatus().equalsIgnoreCase("PENDING")).count();
        double totalRevenue = paymentDAO.findAll().stream()
                .filter(p -> p.getStatus().equalsIgnoreCase("PAID"))
                .mapToDouble(Payment::getAmount).sum();

        ConsoleUtil.printRow("Total Residents:", String.valueOf(residents.size()));
        ConsoleUtil.printRow("Verified:", String.valueOf(verified));
        ConsoleUtil.printRow("Pending Verification:", String.valueOf(pendingVerification));
        ConsoleUtil.printRow("Total Apartments:", String.valueOf(apartments.size()));
        ConsoleUtil.printRow("Occupied / Vacant:", occupied + " / " + vacant);
        ConsoleUtil.printRow("Pending Complaints:", String.valueOf(pendingComplaints));
        ConsoleUtil.printRow("Total Revenue Collected:", String.format("%.2f", totalRevenue));
        ConsoleUtil.pause();
    }

    private static void viewAllResidents() {
        ConsoleUtil.printSubHeader("All Residents");
        List<User> residents = userDAO.findByRole("RESIDENT");
        if (residents.isEmpty()) {
            ConsoleUtil.printInfo("No residents registered yet.");
        } else {
            ConsoleUtil.printTableHeader("ID", "Name", "Flat", "Phone", "Status");
            for (User u : residents) {
                ConsoleUtil.printRow(String.valueOf(u.getId()), u.getFullName(), u.getFlatNumber(),
                        u.getPhone(), u.isVerified() ? "Verified" : "Pending");
            }
        }
        ConsoleUtil.pause();
    }

    private static void verifyResident() {
        ConsoleUtil.printSubHeader("Verify a Resident");
        List<User> pending = userDAO.findByRole("RESIDENT").stream()
                .filter(u -> !u.isVerified()).toList();

        if (pending.isEmpty()) {
            ConsoleUtil.printInfo("No residents awaiting verification.");
            ConsoleUtil.pause();
            return;
        }

        ConsoleUtil.printTableHeader("ID", "Name", "Email", "Flat");
        for (User u : pending) {
            ConsoleUtil.printRow(String.valueOf(u.getId()), u.getFullName(), u.getEmail(), u.getFlatNumber());
        }

        int id = ConsoleUtil.readInt("Enter Resident ID to verify (0 to cancel)");
        if (id == 0) return;

        List<User> all = userDAO.findAll();
        boolean found = false;
        for (User u : all) {
            if (u.getId() == id) {
                u.setVerified(true);
                found = true;
                break;
            }
        }
        if (found) {
            userDAO.updateAll(all);
            ConsoleUtil.printSuccess("Resident #" + id + " has been verified.");
        } else {
            ConsoleUtil.printError("No resident found with that ID.");
        }
        ConsoleUtil.pause();
    }

    private static void apartmentManagement() {
        ConsoleUtil.printMenu("Apartment Management", List.of(
                "View All Apartments",
                "Add New Apartment",
                "Back"
        ));
        int choice = ConsoleUtil.readMenuChoice(3);
        switch (choice) {
            case 1 -> {
                List<Apartment> apartments = apartmentDAO.findAll();
                if (apartments.isEmpty()) {
                    ConsoleUtil.printInfo("No apartments added yet.");
                } else {
                    ConsoleUtil.printTableHeader("ID", "Building", "Block", "Flat", "Status");
                    for (Apartment a : apartments) {
                        ConsoleUtil.printRow(String.valueOf(a.getId()), a.getBuilding(), a.getBlock(),
                                a.getFlatNumber(), a.getStatus());
                    }
                }
                ConsoleUtil.pause();
            }
            case 2 -> {
                String building = ConsoleUtil.readRequired("Building");
                String block = ConsoleUtil.readRequired("Block");
                String floor = ConsoleUtil.readRequired("Floor");
                String flat = ConsoleUtil.readRequired("Flat Number");
                String size = ConsoleUtil.readRequired("Size (e.g. 1200 sqft)");
                ConsoleUtil.printInfo("Status options: OCCUPIED, VACANT, MAINTENANCE");
                String status = ConsoleUtil.readRequired("Status");

                Apartment apartment = new Apartment(
                        apartmentDAO.nextId(), building, block, floor, flat, size, status.toUpperCase());
                apartmentDAO.save(apartment);
                ConsoleUtil.printSuccess("Apartment " + flat + " added.");
                ConsoleUtil.pause();
            }
            case 3 -> ConsoleUtil.clearScreen();
        }
    }

    private static void viewAllComplaints() {
        ConsoleUtil.printSubHeader("All Complaints");
        List<Complaint> complaints = complaintDAO.findAll();
        if (complaints.isEmpty()) {
            ConsoleUtil.printInfo("No complaints submitted yet.");
        } else {
            ConsoleUtil.printTableHeader("ID", "Resident", "Category", "Subject", "Status");
            for (Complaint c : complaints) {
                ConsoleUtil.printRow(String.valueOf(c.getId()), c.getResidentEmail(),
                        c.getCategory(), c.getSubject(), c.getStatus());
            }
        }
        ConsoleUtil.pause();
    }

    private static void updateComplaintStatus() {
        ConsoleUtil.printSubHeader("Update Complaint Status");
        List<Complaint> complaints = complaintDAO.findAll();
        if (complaints.isEmpty()) {
            ConsoleUtil.printInfo("No complaints to update.");
            ConsoleUtil.pause();
            return;
        }
        ConsoleUtil.printTableHeader("ID", "Subject", "Status");
        for (Complaint c : complaints) {
            ConsoleUtil.printRow(String.valueOf(c.getId()), c.getSubject(), c.getStatus());
        }

        int id = ConsoleUtil.readInt("Enter Complaint ID to update (0 to cancel)");
        if (id == 0) return;

        ConsoleUtil.printInfo("Statuses: PENDING, IN_PROGRESS, RESOLVED, REJECTED");
        String newStatus = ConsoleUtil.readRequired("New Status");

        boolean found = false;
        for (Complaint c : complaints) {
            if (c.getId() == id) {
                c.setStatus(newStatus.toUpperCase());
                found = true;
                break;
            }
        }
        if (found) {
            complaintDAO.updateAll(complaints);
            ConsoleUtil.printSuccess("Complaint #" + id + " updated to " + newStatus.toUpperCase());
        } else {
            ConsoleUtil.printError("No complaint found with that ID.");
        }
        ConsoleUtil.pause();
    }

    private static void postNotice(User admin) {
        ConsoleUtil.printSubHeader("Post a Notice");
        String title = ConsoleUtil.readRequired("Title");
        String description = ConsoleUtil.readRequired("Description");
        ConsoleUtil.printInfo("Types: General, Important, Maintenance, Payment, Emergency, Event");
        String type = ConsoleUtil.readRequired("Type");

        Notice notice = new Notice(
                noticeDAO.nextId(), title, description, type, admin.getFullName(), today());
        noticeDAO.save(notice);
        ConsoleUtil.printSuccess("Notice published.");
        ConsoleUtil.pause();
    }

    private static void viewAllPayments() {
        ConsoleUtil.printSubHeader("All Payments");
        List<Payment> payments = paymentDAO.findAll();
        if (payments.isEmpty()) {
            ConsoleUtil.printInfo("No payments recorded yet.");
        } else {
            ConsoleUtil.printTableHeader("ID", "Resident", "Type", "Amount", "Status", "Date");
            for (Payment p : payments) {
                ConsoleUtil.printRow(String.valueOf(p.getId()), p.getResidentEmail(), p.getType(),
                        String.format("%.2f", p.getAmount()), p.getStatus(), p.getDate());
            }
        }
        ConsoleUtil.pause();
    }

    // =================================================================
    // HELPERS
    // =================================================================
    private static String today() {
        return LocalDate.now().toString();
    }


    private static void safe(Runnable action) {
        try {
            action.run();
        } catch (ConsoleUtil.BackSignal e) {
            ConsoleUtil.printInfo("Cancelled. Returning to menu.");
            ConsoleUtil.pause();
        }
    }
}
