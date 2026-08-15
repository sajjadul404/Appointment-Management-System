package com.myhome.model;

/**
 * Apartment
 * ---------
 * A single flat/unit in the building, managed by the Admin.
 */
public class Apartment {

    private int id;
    private String building;
    private String block;
    private String floor;
    private String flatNumber;
    private String size;
    private String status; // OCCUPIED, VACANT, MAINTENANCE

    public Apartment(int id, String building, String block, String floor,
                      String flatNumber, String size, String status) {
        this.id = id;
        this.building = building;
        this.block = block;
        this.floor = floor;
        this.flatNumber = flatNumber;
        this.size = size;
        this.status = status;
    }

    public int getId() { return id; }
    public String getBuilding() { return building; }
    public String getBlock() { return block; }
    public String getFloor() { return floor; }
    public String getFlatNumber() { return flatNumber; }
    public String getSize() { return size; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String toCsvRow() {
        return id + "|" + building + "|" + block + "|" + floor + "|" + flatNumber + "|" + size + "|" + status;
    }

    public static Apartment fromCsvRow(String[] cols) {
        return new Apartment(
                Integer.parseInt(cols[0]),
                cols[1], cols[2], cols[3], cols[4], cols[5], cols[6]
        );
    }
}
