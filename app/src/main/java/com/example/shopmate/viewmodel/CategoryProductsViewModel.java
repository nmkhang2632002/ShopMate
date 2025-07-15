package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.repository.ProductRepository;

import java.util.List;

public class CategoryProductsViewModel extends ViewModel {
    private final ProductRepository productRepository;
    
    public CategoryProductsViewModel() {
        productRepository = new ProductRepository();
    }
    
    public LiveData<List<Product>> getProductsByCategory(int categoryId) {
        return productRepository.getProductsByCategory(categoryId);
    }
    
    public LiveData<Boolean> getIsLoading() {
        return productRepository.getIsLoading();
    }
    
    public LiveData<String> getErrorMessage() {
        return productRepository.getErrorMessage();
    }
} 