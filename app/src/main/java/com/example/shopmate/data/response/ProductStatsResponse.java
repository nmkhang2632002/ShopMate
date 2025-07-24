package com.example.shopmate.data.response;

import com.google.gson.annotations.SerializedName;

public class ProductStatsResponse {
    @SerializedName("productId")
    private int productId;
    
    @SerializedName("productName")
    private String productName;
    
    @SerializedName("productImage")
    private String productImage;
    
    @SerializedName("price")
    private double price;
    
    @SerializedName("category")
    private String category;
    
    @SerializedName("totalOrdered")
    private int totalOrdered;
    
    @SerializedName("orderCount")
    private int orderCount;
    
    @SerializedName("totalRevenue")
    private double totalRevenue;

    // Getters and setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotalOrdered() {
        return totalOrdered;
    }

    public void setTotalOrdered(int totalOrdered) {
        this.totalOrdered = totalOrdered;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
