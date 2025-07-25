package com.example.shopmate.data.model;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String role;
    private String cartID;
    private String chatMessageID;
    private String notificationID;
    private String orderID;

    public RegisterRequest(String username, String password, String email,
                          String phoneNumber, String address) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = "CUSTOMER"; // Default role
        // Optional fields có thể để null
        this.cartID = null;
        this.chatMessageID = null;
        this.notificationID = null;
        this.orderID = null;
    }

    // Getters and setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getCartID() { return cartID; }
    public void setCartID(String cartID) { this.cartID = cartID; }

    public String getChatMessageID() { return chatMessageID; }
    public void setChatMessageID(String chatMessageID) { this.chatMessageID = chatMessageID; }

    public String getNotificationID() { return notificationID; }
    public void setNotificationID(String notificationID) { this.notificationID = notificationID; }

    public String getOrderID() { return orderID; }
    public void setOrderID(String orderID) { this.orderID = orderID; }
}
