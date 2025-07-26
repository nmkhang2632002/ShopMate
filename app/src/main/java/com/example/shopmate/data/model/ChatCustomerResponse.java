package com.example.shopmate.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChatCustomerResponse {
    
    @SerializedName("success")
    private boolean success;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private List<ChatCustomer> data;
    
    public ChatCustomerResponse() {
    }
    
    public ChatCustomerResponse(boolean success, String message, List<ChatCustomer> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public List<ChatCustomer> getData() {
        return data;
    }
    
    public void setData(List<ChatCustomer> data) {
        this.data = data;
    }
} 