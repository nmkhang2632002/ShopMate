package com.example.shopmate.data.model;

import com.google.gson.annotations.SerializedName;

public class ChatCustomer {
    
    @SerializedName("id")
    private int id;
    
    @SerializedName("fullName")
    private String fullName;
    
    @SerializedName("email")
    private String email;
    
    public ChatCustomer() {
    }
    
    public ChatCustomer(int id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
} 