package com.example.shopmate.data.model;

public class UpdatePaymentStatusRequest {
    private String paymentStatus;
    private String note;

    public UpdatePaymentStatusRequest() {}

    public UpdatePaymentStatusRequest(String paymentStatus) {
        this.paymentStatus = paymentStatus;
        this.note = ""; // Default empty note
    }

    public UpdatePaymentStatusRequest(String paymentStatus, String note) {
        this.paymentStatus = paymentStatus;
        this.note = note;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
