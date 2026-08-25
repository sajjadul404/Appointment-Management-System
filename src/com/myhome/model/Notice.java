package com.myhome.model;

/**
 * Notice
 */
public class Notice {

    private int id;
    private String title;
    private String description;
    private String type;   // General, Important, Maintenance, Payment, Emergency, Event
    private String publishedBy;
    private String date;

    public Notice(int id, String title, String description, String type,
                   String publishedBy, String date) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.publishedBy = publishedBy;
        this.date = date;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getType() { return type; }
    public String getPublishedBy() { return publishedBy; }
    public String getDate() { return date; }

    public String toCsvRow() {
        return id + "|" + title + "|" + description + "|" + type + "|" + publishedBy + "|" + date;
    }

    public static Notice fromCsvRow(String[] cols) {
        return new Notice(
                Integer.parseInt(cols[0]),
                cols[1], cols[2], cols[3], cols[4], cols[5]
        );
    }
}
