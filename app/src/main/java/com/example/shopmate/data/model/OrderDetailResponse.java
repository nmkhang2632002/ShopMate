package com.example.shopmate.data.model;

import java.math.BigDecimal;
import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OrderDetailResponse {
    private int id;
    private int cartID;
    private int userID;
    private String paymentMethod;
    private String billingAddress;
    private String orderStatus;
    private String orderDate;
    private List<Order.Payment> payments;
    
    // User information
    private String username;
    private String phoneNumber;
    private String email;
    
    // Cart items (products in this order)
    @SerializedName("cartItems")
    private List<CartItem> cartItems;
    
    // Calculated fields
    private BigDecimal totalAmount;
    private String formattedOrderDate;
    private String latestTransactionId;
    private String paymentStatus;

    // Constructors
    public OrderDetailResponse() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public List<Order.Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Order.Payment> payments) {
        this.payments = payments;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getFormattedOrderDate() {
        return formattedOrderDate;
    }

    public void setFormattedOrderDate(String formattedOrderDate) {
        this.formattedOrderDate = formattedOrderDate;
    }

    public String getLatestTransactionId() {
        return latestTransactionId;
    }

    public void setLatestTransactionId(String latestTransactionId) {
        this.latestTransactionId = latestTransactionId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
