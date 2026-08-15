package com.myhome.util;

import java.util.List;
import java.util.Scanner;

/**
 * ConsoleUtil
 * -----------
 * All console I/O goes through here. Screens are drawn with Unicode
 * box-drawing borders and ANSI colors so the terminal looks like a real
 * app instead of a wall of plain text. Colors auto-disable if the NO_COLOR
 * environment variable is set (https://no-color.org), and every visual
 * gracefully degrades to plain characters if colors somehow don't render.
 */
public final class ConsoleUtil {

    private static final Scanner SCANNER = new Scanner(System.in);

    // ---------------------------------------------------------------
    // COLOR / STYLE (ANSI escape codes)
    // ---------------------------------------------------------------
    private static final boolean COLOR = System.getenv("NO_COLOR") == null;

    private static final String RESET  = COLOR ? "\u001B[0m" : "";
    private static final String BOLD   = COLOR ? "\u001B[1m" : "";

    private static final String CYAN    = COLOR ? "\u001B[36m" : "";
    private static final String MAGENTA = COLOR ? "\u001B[35m" : "";
    private static final String BLUE    = COLOR ? "\u001B[34m" : "";
    private static final String GREEN   = COLOR ? "\u001B[32m" : "";
    private static final String YELLOW  = COLOR ? "\u001B[33m" : "";
    private static final String RED     = COLOR ? "\u001B[31m" : "";
    private static final String GRAY    = COLOR ? "\u001B[90m" : "";
    private static final String WHITE   = COLOR ? "\u001B[97m" : "";

    // ---------------------------------------------------------------
    // BOX DRAWING
    // ---------------------------------------------------------------
    private static final int BOX_WIDTH = 58; // visible text width between the two borders

    private ConsoleUtil() {
    }

    private static String padRight(String text, int width) {
        if (text.length() >= width) {
            return text.substring(0, width);
        }
        return text + " ".repeat(width - text.length());
    }

    private static void boxTop(String color) {
        System.out.println(color + "\u250C" + "\u2500".repeat(BOX_WIDTH + 2) + "\u2510" + RESET);
    }

    private static void boxDivider(String color) {
        System.out.println(color + "\u251C" + "\u2500".repeat(BOX_WIDTH + 2) + "\u2524" + RESET);
    }

    private static void boxBottom(String color) {
        System.out.println(color + "\u2514" + "\u2500".repeat(BOX_WIDTH + 2) + "\u2518" + RESET);
    }

    private static void boxLine(String borderColor, String text, String textColor) {
        System.out.println(borderColor + "\u2502 " + RESET + textColor + padRight(text, BOX_WIDTH)
                + RESET + borderColor + " \u2502" + RESET);
    }

    // ---------------------------------------------------------------
    // SCREEN CONTROL
    // ---------------------------------------------------------------

    /**
     * Actually clears the terminal window (not just blank lines) so each
     * new screen starts fresh. Uses "cls" on Windows and "clear" on
     * macOS/Linux. Falls back to blank-line padding if that isn't possible
     * (e.g. output redirected to a file).
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

    // ---------------------------------------------------------------
    // OUTPUT
    // ---------------------------------------------------------------
    public static void printBanner() {
        System.out.println();
        boxTop(CYAN);
        boxLine(CYAN, centered("N E S T O R A"), BOLD + WHITE);
        boxLine(CYAN, centered("Smart Living. Seamless Community."), CYAN);
        boxBottom(CYAN);
    }

    private static String centered(String text) {
        int pad = Math.max(0, (BOX_WIDTH - text.length()) / 2);
        return " ".repeat(pad) + text;
    }

    /** A boxed screen title used above a block of info/detail content. */
    public static void printHeader(String title) {
        System.out.println();
        boxTop(MAGENTA);
        boxLine(MAGENTA, title.toUpperCase(), BOLD + WHITE);
        boxBottom(MAGENTA);
    }

    /** A lighter section title used inside a screen (e.g. "My Profile"). */
    public static void printSubHeader(String title) {
        System.out.println();
        System.out.println(BLUE + BOLD + "  \u25B8 " + title + RESET);
        System.out.println(GRAY + "  " + "\u2500".repeat(title.length() + 2) + RESET);
    }

    /** A boxed, numbered menu the user picks an option from. */
    public static void printMenu(String title, List<String> options) {
        System.out.println();
        boxTop(CYAN);
        boxLine(CYAN, title.toUpperCase(), BOLD + WHITE);
        boxDivider(CYAN);
        for (int i = 0; i < options.size(); i++) {
            boxLine(CYAN, "  [" + (i + 1) + "]  " + options.get(i), WHITE);
        }
        boxBottom(CYAN);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "  \u2714  " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "  \u2716  " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(BLUE + "  \u2139  " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "  \u26A0  " + message + RESET);
    }

    /** A bold table header row, followed by a matching underline. */
    public static void printTableHeader(String... columns) {
        StringBuilder sb = new StringBuilder("  ");
        for (String col : columns) {
            sb.append(String.format("%-18s", col));
        }
        System.out.println(BOLD + WHITE + sb + RESET);
        System.out.println(GRAY + "  " + "\u2500".repeat(Math.min(sb.length() - 2, 90)) + RESET);
    }

    /** A plain data row aligned under printTableHeader's columns. */
    public static void printRow(String... columns) {
        StringBuilder sb = new StringBuilder("  ");
        for (String col : columns) {
            sb.append(String.format("%-18s", col));
        }
        System.out.println(sb);
    }

    // ---------------------------------------------------------------
    // INPUT
    // ---------------------------------------------------------------

    /**
     * Thrown when the user types "back" at a form field, so any multi-step
     * screen (registration, login, submitting a complaint, etc.) can be
     * cancelled cleanly and control returns to the calling menu.
     */
    public static class BackSignal extends RuntimeException {
    }

    public static String readLine(String prompt) {
        System.out.print(GRAY + "  " + prompt + ": " + RESET);
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
            System.out.print(GRAY + "  " + prompt + " " + RESET + GRAY + "(or type 'back' to cancel): " + RESET);
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
        System.out.print(GRAY + "  " + prompt + " " + RESET + GRAY + "(or type 'back' to cancel): " + RESET);
        String value = SCANNER.nextLine().trim();
        if (value.equalsIgnoreCase("back")) {
            throw new BackSignal();
        }
        return value;
    }

    public static void pause() {
        System.out.print(GRAY + "  Press ENTER to continue..." + RESET);
        SCANNER.nextLine();
        clearScreen();
    }
}
