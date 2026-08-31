package com.myhome.dao;

import com.myhome.model.Payment;
import com.myhome.util.CsvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * PaymentDAO
 * --------
 * data/payments.csv — simulated rent/utility/maintenance payments.
 * Columns: id|residentEmail|type|amount|method|status|date|transactionId
 */
public class PaymentDAO {

    private static final String PATH = "data/payments.csv";
    private static final String HEADER = "id|residentEmail|type|amount|method|status|date|transactionId";

    public PaymentDAO() {
        CsvUtil.ensureFile(PATH, HEADER);
    }

    public List<Payment> findAll() {
        List<Payment> list = new ArrayList<>();
        for (String[] cols : CsvUtil.readRows(PATH)) {
            list.add(Payment.fromCsvRow(cols));
        }
        return list;
    }

    public List<Payment> findByResident(String email) {
        List<Payment> list = new ArrayList<>();
        for (Payment p : findAll()) {
            if (p.getResidentEmail().equalsIgnoreCase(email)) {
                list.add(p);
            }
        }
        return list;
    }

    public Payment save(Payment payment) {
        CsvUtil.appendRow(PATH, payment.toCsvRow());
        return payment;
    }

    public int nextId() {
        return CsvUtil.nextId(PATH);
    }
}
