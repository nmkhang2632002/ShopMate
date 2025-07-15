package com.example.shopmate.data.model;

public class Category {
    private int id;
    private String categoryName;

    public Category() {}

    public Category(int id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    // Getters
    public int getId() { return id; }
    public String getCategoryName() { return categoryName; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
} 