package com.example.shopmate.data.model;



public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    // Getters
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public T getData() { return data; }

    // Setters
    public void setStatus(int status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setData(T data) { this.data = data; }

    // Helper method to check if response is successful
    public boolean isSuccessful() {
        return status == 1000;
    }

    // Alias method for consistency
    public boolean isSuccess() {
        return isSuccessful();
    }
}
