package com.nestora.dao;

import com.nestora.model.Complaint;
import com.nestora.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ComplaintDAO
 * ------------
 * data/complaints.csv — Help Desk tickets.
 * Columns: id|residentEmail|category|subject|description|status|date
 */
public class ComplaintDAO {

    private static final String PATH = "data/complaints.csv";
    private static final String HEADER = "id|residentEmail|category|subject|description|status|date";

    public ComplaintDAO() {
        CsvUtil.ensureFile(PATH, HEADER);
    }

    public List<Complaint> findAll() {
        List<Complaint> list = new ArrayList<>();
        for (String[] cols : CsvUtil.readRows(PATH)) {
            list.add(Complaint.fromCsvRow(cols));
        }
        return list;
    }

    public List<Complaint> findByResident(String email) {
        List<Complaint> list = new ArrayList<>();
        for (Complaint c : findAll()) {
            if (c.getResidentEmail().equalsIgnoreCase(email)) {
                list.add(c);
            }
        }
        return list;
    }

    public Complaint save(Complaint complaint) {
        CsvUtil.appendRow(PATH, complaint.toCsvRow());
        return complaint;
    }

    public void updateAll(List<Complaint> complaints) {
        List<String> rows = new ArrayList<>();
        for (Complaint c : complaints) {
            rows.add(c.toCsvRow());
        }
        CsvUtil.writeAll(PATH, HEADER, rows);
    }

    public int nextId() {
        return CsvUtil.nextId(PATH);
    }
}
