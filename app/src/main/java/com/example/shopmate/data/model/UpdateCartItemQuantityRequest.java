package com.example.shopmate.data.model;

import java.io.Serializable;

public class UpdateCartItemQuantityRequest implements Serializable {
    private int cartItemID;
    private int quantity;

    public UpdateCartItemQuantityRequest() {}

    public UpdateCartItemQuantityRequest(int cartItemID, int quantity) {
        this.cartItemID = cartItemID;
        this.quantity = quantity;
    }

    // Getters
    public int getCartItemID() { return cartItemID; }
    public int getQuantity() { return quantity; }

    // Setters
    public void setCartItemID(int productID) { this.cartItemID = cartItemID; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}