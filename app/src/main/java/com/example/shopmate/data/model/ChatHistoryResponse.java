package com.example.shopmate.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public class ChatHistoryResponse {
    @SerializedName("aiMessages")
    private List<ChatMessage> aiMessages;
    
    @SerializedName("customerMessages")
    private Map<String, List<ChatMessage>> customerMessages;
    
    @SerializedName("adminMessages")
    private List<ChatMessage> adminMessages;

    public List<ChatMessage> getAiMessages() {
        return aiMessages;
    }

    public void setAiMessages(List<ChatMessage> aiMessages) {
        this.aiMessages = aiMessages;
    }

    public Map<String, List<ChatMessage>> getCustomerMessages() {
        return customerMessages;
    }

    public void setCustomerMessages(Map<String, List<ChatMessage>> customerMessages) {
        this.customerMessages = customerMessages;
    }

    public List<ChatMessage> getAdminMessages() {
        return adminMessages;
    }

    public void setAdminMessages(List<ChatMessage> adminMessages) {
        this.adminMessages = adminMessages;
    }
} 