package com.nestora.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

/**
 * CsvUtil
 * -------
 * Tiny, dependency-free helper for reading/writing pipe-delimited ".csv"
 * files under the data/ folder. A pipe ("|") is used instead of a comma so
 * that free-text fields (descriptions, notice bodies, etc.) don't need
 * quoting/escaping logic -- just avoid typing "|" in text input.
 *
 * Every data file has a header row (skipped on read, written once on
 * creation) so the files stay human-readable if you open them directly.
 */
public final class CsvUtil {

    public static final String DELIMITER = "\\|"; // regex form, for split()
    public static final String DELIMITER_RAW = "|";

    private CsvUtil() {
    }

    /** Makes sure the data file exists, creating it (with the given header) if not. */
    public static void ensureFile(String path, String header) {
        try {
            Path p = Paths.get(path);
            if (p.getParent() != null) {
                Files.createDirectories(p.getParent());
            }
            if (!Files.exists(p)) {
                Files.writeString(p, header + System.lineSeparator());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Reads every data row (header excluded) as raw column arrays. */
    public static List<String[]> readRows(String path) {
        List<String[]> rows = new ArrayList<>();
        Path p = Paths.get(path);
        if (!Files.exists(p)) {
            return rows;
        }
        try {
            List<String> lines = Files.readAllLines(p);
            for (int i = 1; i < lines.size(); i++) { // skip header
                String line = lines.get(i);
                if (line.isBlank()) continue;
                rows.add(line.split(DELIMITER, -1));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return rows;
    }

    /** Appends a single already-formatted row to the file. */
    public static void appendRow(String path, String row) {
        try {
            Files.writeString(Paths.get(path), row + System.lineSeparator(),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Overwrites the whole file (header + all rows) -- used for updates/deletes. */
    public static void writeAll(String path, String header, List<String> rows) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(header).append(System.lineSeparator());
            for (String row : rows) {
                sb.append(row).append(System.lineSeparator());
            }
            Files.writeString(Paths.get(path), sb.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Returns the next auto-increment id: (number of existing rows) + 1. */
    public static int nextId(String path) {
        return readRows(path).size() + 1;
    }
}
