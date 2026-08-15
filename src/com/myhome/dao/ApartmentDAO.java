package com.myhome.dao;

import com.myhome.model.Apartment;
import com.myhome.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * ApartmentDAO
 * ------------
 * data/apartments.csv — building/block/floor/flat inventory managed by Admin.
 * Columns: id|building|block|floor|flatNumber|size|status
 */
public class ApartmentDAO {

    private static final String PATH = "data/apartments.csv";
    private static final String HEADER = "id|building|block|floor|flatNumber|size|status";

    public ApartmentDAO() {
        CsvUtil.ensureFile(PATH, HEADER);
    }

    public List<Apartment> findAll() {
        List<Apartment> list = new ArrayList<>();
        for (String[] cols : CsvUtil.readRows(PATH)) {
            list.add(Apartment.fromCsvRow(cols));
        }
        return list;
    }

    public Apartment save(Apartment apartment) {
        CsvUtil.appendRow(PATH, apartment.toCsvRow());
        return apartment;
    }

    public void updateAll(List<Apartment> apartments) {
        List<String> rows = new ArrayList<>();
        for (Apartment a : apartments) {
            rows.add(a.toCsvRow());
        }
        CsvUtil.writeAll(PATH, HEADER, rows);
    }

    public int nextId() {
        return CsvUtil.nextId(PATH);
    }
}
