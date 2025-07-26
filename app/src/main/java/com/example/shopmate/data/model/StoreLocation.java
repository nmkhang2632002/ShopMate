package com.example.shopmate.data.model;

public class StoreLocation {
    private int id;
    private double latitude;
    private double longitude;
    private String storeName;
    private String address;

    public StoreLocation(int id, double latitude, double longitude, String storeName, String address) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.storeName = storeName;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
} 