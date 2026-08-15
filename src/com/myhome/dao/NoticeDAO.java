package com.myhome.dao;

import com.myhome.model.Notice;
import com.myhome.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * NoticeDAO
 * ---------
 * data/notices.csv — Admin-published notice board entries.
 * Columns: id|title|description|type|publishedBy|date
 */
public class NoticeDAO {

    private static final String PATH = "data/notices.csv";
    private static final String HEADER = "id|title|description|type|publishedBy|date";

    public NoticeDAO() {
        CsvUtil.ensureFile(PATH, HEADER);
    }

    public List<Notice> findAll() {
        List<Notice> list = new ArrayList<>();
        for (String[] cols : CsvUtil.readRows(PATH)) {
            list.add(Notice.fromCsvRow(cols));
        }
        return list;
    }

    public Notice save(Notice notice) {
        CsvUtil.appendRow(PATH, notice.toCsvRow());
        return notice;
    }

    public int nextId() {
        return CsvUtil.nextId(PATH);
    }
}
