package com.example.shopmate.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {

    @SerializedName("id")
    private int id;
    
    @SerializedName("cartID")
    private int cartId;
    
    @SerializedName("userID")
    private int userId;
    
    @SerializedName("userName")
    private String userName;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("billingAddress")
    private String billingAddress;
    
    @SerializedName("orderStatus")
    private String orderStatus;
    
    @SerializedName("status")
    private String status;

    @SerializedName("orderDate")
    private Date orderDate;

    @SerializedName("totalAmount")
    private double totalAmount;

    @SerializedName("totalItems")
    private int totalItems;

    @SerializedName("note")
    private String note;

    @SerializedName("payments")
    private List<Payment> payments;
    
    @SerializedName("orderItems")
    private List<OrderDetail> orderItems;
    
    @SerializedName("cartItems")
    private List<CartItem> cartItems;

    public Order() {}

    // Getters
    public int getId() { return id; }
    public int getCartId() { return cartId; }
    public int getUserId() { return userId; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getBillingAddress() { return billingAddress; }
    public String getOrderStatus() { return orderStatus; }
    public String getStatus() { return status != null ? status : orderStatus; }
    public Date getOrderDate() { return orderDate; }
    public double getTotalAmount() { return totalAmount; }
    public int getTotalItems() { return totalItems; }
    public String getNote() { return note; }
    public List<Payment> getPayments() { return payments; }
    public List<OrderDetail> getOrderItems() { return orderItems; }
    public List<CartItem> getCartItems() { return cartItems; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCartId(int cartId) { this.cartId = cartId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setUserName(String userName) { this.userName = userName; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setBillingAddress(String billingAddress) { this.billingAddress = billingAddress; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }
    public void setStatus(String status) { this.status = status; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
    public void setNote(String note) { this.note = note; }
    public void setPayments(List<Payment> payments) { this.payments = payments; }
    public void setOrderItems(List<OrderDetail> orderItems) { this.orderItems = orderItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

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