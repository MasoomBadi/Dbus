package com.sam.dbus;

public class City {

    private int BusNo;
    private String BusNoi;
    private String City;
    private String PickupName;

    public City() {
    }


    public City(String City, int BusNo) {
        this.City = City;
        this.BusNo = BusNo;
    }

    public City(String BusNoi) {
        this.BusNoi = BusNoi;
    }

    public void setName(String City) {
        this.City = City;
    }

    public void setId(int BusNo) {
        this.BusNo = BusNo;
    }

    public String getName() {
        return this.City;
    }

    public int getId() {
        return this.BusNo;
    }

    public String getBusNo() {
        return this.BusNoi;
    }
}
