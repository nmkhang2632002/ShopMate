package com.example.shopmate.data.model;

import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String productName;
    private String briefDescription;
    private String fullDescription;
    private double price;
    private String imageURL;
    private int categoryID;
    private String categoryName;

    public Product() {}

    public Product(int id, String productName, String briefDescription, String fullDescription, 
                   double price, String imageURL, int categoryID, String categoryName) {
        this.id = id;
        this.productName = productName;
        this.briefDescription = briefDescription;
        this.fullDescription = fullDescription;
        this.price = price;
        this.imageURL = imageURL;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    // Getters
    public int getId() { return id; }
    public String getProductName() { return productName; }
    public String getBriefDescription() { return briefDescription; }
    public String getFullDescription() { return fullDescription; }
    public double getPrice() { return price; }
    public String getImageURL() { return imageURL; }
    public int getCategoryID() { return categoryID; }
    public String getCategoryName() { return categoryName; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
    public void setPrice(double price) { this.price = price; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    // Helper method to format price
    public String getFormattedPrice() {
        return String.format("$%.2f", price);
    }
} 