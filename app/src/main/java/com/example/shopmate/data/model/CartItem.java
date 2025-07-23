package com.example.shopmate.data.model;

import java.io.Serializable;
import com.example.shopmate.util.CurrencyUtils;

public class CartItem implements Serializable {
    private int id;
    private int productID;
    private String productName;
    private String productImage; // Thêm field để lưu URL hình ảnh sản phẩm
    private int quantity;
    private double price;
    private double subtotal;

    public CartItem() {}

    public CartItem(int id, int productID, String productName, int quantity, double price, double subtotal) {
        this.id = id;
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Constructor với productImage
    public CartItem(int id, int productID, String productName, String productImage, int quantity, double price, double subtotal) {
        this.id = id;
        this.productID = productID;
        this.productName = productName;
        this.productImage = productImage;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Getters
    public int getId() { return id; }
    public int getProductID() { return productID; }
    public String getProductName() { return productName; }
    public String getProductImage() { return productImage; } // Getter cho productImage
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProductID(int productID) { this.productID = productID; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductImage(String productImage) { this.productImage = productImage; } // Setter cho productImage
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }

    // Helper methods
    public double getSubtotal() {
        return this.subtotal;
    }

    public String getFormattedPrice() {
        return CurrencyUtils.formatVND(price);
    }

    public String getFormattedSubtotal() {
        return CurrencyUtils.formatVND(getSubtotal());
    }
} 