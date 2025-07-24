package com.example.shopmate.data.response;

import com.example.shopmate.data.model.Product;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductSearchResponse {
    @SerializedName("content")
    private List<Product> content;
    
    @SerializedName("totalElements")
    private long totalElements;
    
    @SerializedName("totalPages")
    private int totalPages;
    
    @SerializedName("number")
    private int number;
    
    @SerializedName("size")
    private int size;
    
    @SerializedName("first")
    private boolean first;
    
    @SerializedName("last")
    private boolean last;
    
    @SerializedName("empty")
    private boolean empty;

    // Constructors
    public ProductSearchResponse() {}

    // Getters and Setters
    public List<Product> getContent() {
        return content;
    }

    public void setContent(List<Product> content) {
        this.content = content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }

    @Override
    public String toString() {
        return "ProductSearchResponse{" +
                "content=" + (content != null ? content.size() : 0) +
                ", totalElements=" + totalElements +
                ", totalPages=" + totalPages +
                ", number=" + number +
                ", size=" + size +
                '}';
    }
}
