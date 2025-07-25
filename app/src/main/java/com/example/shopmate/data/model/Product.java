package com.example.shopmate.data.model;

import java.io.Serializable;
import com.example.shopmate.util.CurrencyUtils;
import com.google.gson.annotations.SerializedName;

public class Product implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("productName")
    private String productName;

    @SerializedName("briefDescription")
    private String briefDescription;

    @SerializedName("fullDescription")
    private String fullDescription;

    @SerializedName("technicalSpecifications")
    private String technicalSpecifications;

    @SerializedName("price")
    private double price;

    @SerializedName(value = "imageURL", alternate = {"productImage"})
    private String imageURL;

    @SerializedName("categoryID")
    private int categoryID;

    @SerializedName(value = "categoryName", alternate = {"category"})
    private String categoryName;

    @SerializedName("totalOrdered")
    private int totalOrdered;

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
        this.totalOrdered = 0;
    }

    public Product(int id, String productName, String briefDescription, String fullDescription,
                   String technicalSpecifications, double price, String imageURL, int categoryID, String categoryName) {
        this.id = id;
        this.productName = productName;
        this.briefDescription = briefDescription;
        this.fullDescription = fullDescription;
        this.technicalSpecifications = technicalSpecifications;
        this.price = price;
        this.imageURL = imageURL;
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.totalOrdered = 0;
    }

    // Getters
    public int getId() { return id; }
    public String getProductName() { return productName; }
    public String getBriefDescription() { return briefDescription; }
    public String getFullDescription() { return fullDescription; }
    public String getTechnicalSpecifications() { return technicalSpecifications; }
    public double getPrice() { return price; }
    public String getImageURL() { return imageURL; }
    public int getCategoryID() { return categoryID; }
    public String getCategoryName() { return categoryName; }
    public int getTotalOrdered() { return totalOrdered; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setBriefDescription(String briefDescription) { this.briefDescription = briefDescription; }
    public void setFullDescription(String fullDescription) { this.fullDescription = fullDescription; }
    public void setTechnicalSpecifications(String technicalSpecifications) { this.technicalSpecifications = technicalSpecifications; }
    public void setPrice(double price) { this.price = price; }
    public void setImageURL(String imageURL) { this.imageURL = imageURL; }
    public void setCategoryID(int categoryID) { this.categoryID = categoryID; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setTotalOrdered(int totalOrdered) { this.totalOrdered = totalOrdered; }

    // Helper method to format price
    public String getFormattedPrice() {
        return CurrencyUtils.formatVND(price);
    }
}