package com.example.shopmate.data.model;

import java.io.Serializable;
import com.google.gson.annotations.SerializedName;

public class Category implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("categoryName")
    private String categoryName;

    @SerializedName("description")
    private String description;

    @SerializedName("icon")
    private String icon;

    public Category() {}

    public Category(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public Category(int id, String categoryName, String description, String icon) {
        this.id = id;
        this.categoryName = categoryName;
        this.description = description;
        this.icon = icon;
    }

    // Getters
    public int getId() { return id; }
    public String getCategoryName() { return categoryName; }
    public String getDescription() { return description; }
    public String getIcon() { return icon; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public void setDescription(String description) { this.description = description; }
    public void setIcon(String icon) { this.icon = icon; }
}
