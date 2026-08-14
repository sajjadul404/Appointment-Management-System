package com.nestora.util;

import java.util.List;
import java.util.Scanner;

/**
 * ConsoleUtil
 * -----------
 * All console I/O goes through here: reading input, printing menus,
 * headers, tables and messages -- kept consistent so every screen in the
 * app looks and behaves the same way.
 */
public final class ConsoleUtil {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final String LINE = "=".repeat(60);
    private static final String THIN_LINE = "-".repeat(60);

    private ConsoleUtil() {
    }

    // ---------------------------------------------------------------
    // OUTPUT
    // ---------------------------------------------------------------

    /**
     * Actually clears the terminal window (not just blank lines) so each
     * new screen starts fresh. Uses "cls" on Windows and "clear" on
     * macOS/Linux. If that's not possible for some reason (e.g. output is
     * being redirected to a file), it falls back to printing enough blank
     * lines to push old content out of view.
     */
    public static void clearScreen() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            ProcessBuilder pb = os.contains("win")
                    ? new ProcessBuilder("cmd", "/c", "cls")
                    : new ProcessBuilder("clear");
            pb.inheritIO().start().waitFor();
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    public static void printBanner() {
        System.out.println(LINE);
        System.out.println("   MY HOME   -   Apartment Management System");
        System.out.println("   \"Smart Living. Seamless Community.\"");
        System.out.println(LINE);
    }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println(LINE);
        System.out.println("  " + title.toUpperCase());
        System.out.println(LINE);
    }

    public static void printSubHeader(String title) {
        System.out.println();
        System.out.println(THIN_LINE);
        System.out.println("  " + title);
        System.out.println(THIN_LINE);
    }

    public static void printMenu(String title, List<String> options) {
        printHeader(title);
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  [" + (i + 1) + "] " + options.get(i));
        }
        System.out.println(THIN_LINE);
    }

    public static void printSuccess(String message) {
        System.out.println("  \u2714  " + message);
    }

    public static void printError(String message) {
        System.out.println("  \u2716  " + message);
    }

    public static void printInfo(String message) {
        System.out.println("  \u2139  " + message);
    }

    public static void printRow(String... columns) {
        StringBuilder sb = new StringBuilder("  ");
        for (String col : columns) {
            sb.append(String.format("%-18s", col));
        }
        System.out.println(sb);
    }

    /**
     * Thrown when the user types "back" at a form field, so any multi-step
     * screen (registration, login, submitting a complaint, etc.) can be
     * cancelled cleanly and control returns to the calling menu.
     */
    public static class BackSignal extends RuntimeException {
    }

    // ---------------------------------------------------------------
    // INPUT
    // ---------------------------------------------------------------
    public static String readLine(String prompt) {
        System.out.print("  " + prompt + ": ");
        return SCANNER.nextLine().trim();
    }

    /**
     * Non-empty text input; keeps asking until something is typed.
     * Typing "back" (any case) at this prompt cancels the current screen
     * and throws BackSignal, which the calling menu catches to return
     * safely to where the user came from.
     */
    public static String readRequired(String prompt) {
        String value;
        do {
            System.out.print("  " + prompt + " (or type 'back' to cancel): ");
            value = SCANNER.nextLine().trim();
            if (value.equalsIgnoreCase("back")) {
                throw new BackSignal();
            }
            if (value.isBlank()) {
                printError("This field can't be empty.");
            }
        } while (value.isBlank());
        return value;
    }

    public static int readInt(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Integer.parseInt(raw.trim());
            } catch (NumberFormatException e) {
                printError("Please enter a valid number.");
            }
        }
    }

    public static int readMenuChoice(int optionCount) {
        while (true) {
            int choice = readInt("Select an option (1-" + optionCount + ")");
            if (choice >= 1 && choice <= optionCount) {
                return choice;
            }
            printError("Please choose a number between 1 and " + optionCount + ".");
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Double.parseDouble(raw.trim());
            } catch (NumberFormatException e) {
                printError("Please enter a valid amount.");
            }
        }
    }

    /**
     * Reads a password.
     *
     * Note: this intentionally always goes through the same Scanner as
     * every other prompt (not java.io.Console). Mixing Console.readPassword()
     * with a Scanner on System.in causes the two to fight over the input
     * buffer -- on many Windows terminals and IDE run windows that makes
     * password input appear to hang or silently drop keystrokes. Typing
     * the password visibly, reliably, is better than a masked prompt that
     * doesn't actually work.
     */
    public static String readPassword(String prompt) {
        System.out.print("  " + prompt + " (or type 'back' to cancel): ");
        String value = SCANNER.nextLine().trim();
        if (value.equalsIgnoreCase("back")) {
            throw new BackSignal();
        }
        return value;
    }

    public static void pause() {
        System.out.print("  Press ENTER to continue...");
        SCANNER.nextLine();
        clearScreen();
    }
}
