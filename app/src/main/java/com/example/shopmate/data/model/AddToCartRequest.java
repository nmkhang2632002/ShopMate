package com.example.shopmate.data.model;

import java.io.Serializable;

public class    AddToCartRequest implements Serializable {
    private int productID;
    private int quantity;
    private double price;

    public AddToCartRequest() {}

    public AddToCartRequest(int productID, int quantity, double price) {
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public int getProductID() { return productID; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // Setters
    public void setProductID(int productID) { this.productID = productID; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
} 