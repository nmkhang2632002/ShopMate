package com.example.shopmate.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import com.example.shopmate.util.CurrencyUtils;
import com.google.gson.annotations.SerializedName;

public class CartItem implements Serializable {
    private int id;
    @SerializedName("productID")
    private int productID;
    @SerializedName("productName")
    private String productName;
    @SerializedName("productImage")
    private String productImage; // Thêm field để lưu URL hình ảnh sản phẩm
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    public CartItem() {}

    public CartItem(int id, int productID, String productName, int quantity, BigDecimal price, BigDecimal subtotal) {
        this.id = id;
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    // Constructor với productImage
    public CartItem(int id, int productID, String productName, String productImage, int quantity, BigDecimal price, BigDecimal subtotal) {
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
    public BigDecimal getPrice() { return price; }
    public BigDecimal getSubtotal() { return subtotal; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setProductID(int productID) { this.productID = productID; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductImage(String productImage) { this.productImage = productImage; } // Setter cho productImage
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    // Helper methods
    public String getFormattedPrice() {
        return CurrencyUtils.formatVND(price);
    }

    public String getFormattedSubtotal() {
        return CurrencyUtils.formatVND(getSubtotal());
    }
} 