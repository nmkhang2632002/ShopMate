package com.example.shopmate.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("cartID")
    private int cartId;
    
    @SerializedName("userID")
    private int userId;
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("billingAddress")
    private String billingAddress;
    
    @SerializedName("orderStatus")
    private String orderStatus;
    
    @SerializedName("orderDate")
    private String orderDate;
    
    @SerializedName("payments")
    private List<Payment> payments;
    
    // Getters
    public int getId() {
        return id;
    }
    
    public int getCartId() {
        return cartId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public String getBillingAddress() {
        return billingAddress;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }
    
    public String getOrderDate() {
        return orderDate;
    }
    
    public List<Payment> getPayments() {
        return payments;
    }
    
    // Inner Payment class
    public static class Payment {
        @SerializedName("id")
        private int id;
        
        @SerializedName("orderID")
        private int orderId;
        
        @SerializedName("amount")
        private double amount;
        
        @SerializedName("paymentDate")
        private String paymentDate;
        
        @SerializedName("paymentStatus")
        private String paymentStatus;
        
        // Getters
        public int getId() {
            return id;
        }
        
        public int getOrderId() {
            return orderId;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public String getPaymentDate() {
            return paymentDate;
        }
        
        public String getPaymentStatus() {
            return paymentStatus;
        }
    }
}