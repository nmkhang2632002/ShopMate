package com.example.shopmate.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private int id;
    private int productID;
    private String productName;
    private int quantity;
    private double price;

    public CartItem() {}

    public CartItem(int id, int productID, String productName, int quantity, double price) {
        this.id = id;
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public int getId() { return id; }
    public int getProductID() { return productID; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProductID(int productID) { this.productID = productID; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }

    // Helper methods
    public double getSubtotal() {
        return quantity * price;
    }

    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }

    public String getFormattedSubtotal() {
        return String.format("$%.2f", getSubtotal());
    }
} 