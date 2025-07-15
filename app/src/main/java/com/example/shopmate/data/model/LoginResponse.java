package com.example.shopmate.data.model;

public class LoginResponse {
    private User user;
    private String accessToken;
    private boolean authenticated;

    public LoginResponse() {}

    public LoginResponse(User user, String accessToken, boolean authenticated) {
        this.user = user;
        this.accessToken = accessToken;
        this.authenticated = authenticated;
    }

    // Getters
    public User getUser() { return user; }
    public String getAccessToken() { return accessToken; }
    public boolean isAuthenticated() { return authenticated; }

    // Setters
    public void setUser(User user) { this.user = user; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setAuthenticated(boolean authenticated) { this.authenticated = authenticated; }

    // Inner User class
    public static class User {
        private int id;
        private String username;
        private String email;
        private String phoneNumber;
        private String address;
        private String role;
        private Object cart;
        private Object chatMessage;
        private Object notification;
        private Object order;

        public User() {}

        // Getters
        public int getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
        public String getAddress() { return address; }
        public String getRole() { return role; }
        public Object getCart() { return cart; }
        public Object getChatMessage() { return chatMessage; }
        public Object getNotification() { return notification; }
        public Object getOrder() { return order; }

        // Setters
        public void setId(int id) { this.id = id; }
        public void setUsername(String username) { this.username = username; }
        public void setEmail(String email) { this.email = email; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
        public void setAddress(String address) { this.address = address; }
        public void setRole(String role) { this.role = role; }
        public void setCart(Object cart) { this.cart = cart; }
        public void setChatMessage(Object chatMessage) { this.chatMessage = chatMessage; }
        public void setNotification(Object notification) { this.notification = notification; }
        public void setOrder(Object order) { this.order = order; }
    }
} 