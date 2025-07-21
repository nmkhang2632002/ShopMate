package com.example.shopmate.data.model;

public class CreateOrderRequest {
    private String paymentMethod;
    private String billingAddress;
    
    public CreateOrderRequest(String paymentMethod, String billingAddress) {
        this.paymentMethod = paymentMethod;
        this.billingAddress = billingAddress;
    }
}