package com.example.shopmate.data.model;

public class Payment {
    private int id;
    private int orderID;
    private double amount;
    private String paymentDate;
    private String paymentStatus;

    public Payment() {}

    public Payment(int id, int orderID, double amount, String paymentDate, String paymentStatus) {
        this.id = id;
        this.orderID = orderID;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentStatus = paymentStatus;
    }

    // Getters
    public int getId() { return id; }
    public int getOrderID() { return orderID; }
    public double getAmount() { return amount; }
    public String getPaymentDate() { return paymentDate; }
    public String getPaymentStatus() { return paymentStatus; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setOrderID(int orderID) { this.orderID = orderID; }
    public void setAmount(double amount) { this.amount = amount; }
    public void setPaymentDate(String paymentDate) { this.paymentDate = paymentDate; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
}
