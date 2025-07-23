package com.example.shopmate.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.example.shopmate.util.CurrencyUtils;

public class Cart implements Serializable {
    private int id;
    private int userID;
    private double totalPrice;
    private String status;
    private List<CartItem> cartItems;

    public Cart() {
        cartItems = new ArrayList<>();
    }

    public Cart(int id, int userID, double totalPrice, String status, List<CartItem> cartItems) {
        this.id = id;
        this.userID = userID;
        this.totalPrice = totalPrice;
        this.status = status;
        this.cartItems = cartItems != null ? cartItems : new ArrayList<>();
    }

    // Getters
    public int getId() { return id; }
    public int getUserID() { return userID; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public List<CartItem> getCartItems() { return cartItems; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserID(int userID) { this.userID = userID; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

    // Helper methods
    public int getTotalItems() {
        int total = 0;
        for (CartItem item : cartItems) {
            total += item.getQuantity();
        }
        return total;
    }

    public String getFormattedTotalPrice() {
        return CurrencyUtils.formatVND(totalPrice);
    }

    public boolean isEmpty() {
        return cartItems == null || cartItems.isEmpty();
    }

    public List<CartItem> getItems()  {
        return cartItems;
    }
} 