package com.example.shopmate.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ChatMessage {
    @SerializedName("userID")
    private int userId;
    
    @SerializedName("receiverID")
    private int receiverId;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("sentAt")
    private Date sentAt;
    
    @SerializedName("fromAI")
    private boolean fromAI;
    
    @SerializedName("forwardedToHuman")
    private boolean forwardedToHuman;
    
    @SerializedName("toAdmin")
    private boolean toAdmin;

    // Default constructor
    public ChatMessage() {
    }

    // Constructor for sending new messages
    public ChatMessage(int userId, int receiverId, String message) {
        this.userId = userId;
        this.receiverId = receiverId;
        this.message = message;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isFromAI() {
        return fromAI;
    }

    public void setFromAI(boolean fromAI) {
        this.fromAI = fromAI;
    }

    public boolean isForwardedToHuman() {
        return forwardedToHuman;
    }

    public void setForwardedToHuman(boolean forwardedToHuman) {
        this.forwardedToHuman = forwardedToHuman;
    }

    public boolean isToAdmin() {
        return toAdmin;
    }

    public void setToAdmin(boolean toAdmin) {
        this.toAdmin = toAdmin;
    }
} 