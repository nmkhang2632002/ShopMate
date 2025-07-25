package com.example.shopmate.data.model;

import com.google.gson.annotations.SerializedName;

public class UpdateOrderStatusRequest {
    @SerializedName("status")
    private String status;

    @SerializedName("note")
    private String note;

    public UpdateOrderStatusRequest() {}

    public UpdateOrderStatusRequest(String status, String note) {
        this.status = status;
        this.note = note;
    }

    // Getters and setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
