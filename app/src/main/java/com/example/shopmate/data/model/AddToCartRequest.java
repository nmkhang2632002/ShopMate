package com.example.shopmate.data.model;

import java.io.Serializable;

public class    AddToCartRequest implements Serializable {
    private int productID;
    private int quantity;

    public AddToCartRequest() {
    }

    public AddToCartRequest(int productID, int quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }

    // Getters
    public int getProductID() { return productID; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setProductID(int productID) { this.productID = productID; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
} 