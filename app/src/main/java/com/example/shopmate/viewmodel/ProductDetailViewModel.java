package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.model.Product;
import com.example.shopmate.repository.ProductRepository;
import com.example.shopmate.repository.CartRepository;

public class ProductDetailViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private int currentProductId = -1;
    private final MutableLiveData<Boolean> addToCartSuccess = new MutableLiveData<>(false);

    // Current user ID (in a real app, this would come from user authentication)
    private final int userId = 1;

    public ProductDetailViewModel() {
        productRepository = new ProductRepository();
        cartRepository = CartRepository.getInstance();
    }

    public LiveData<Product> getProduct(int productId) {
        if (currentProductId != productId) {
            currentProductId = productId;
            return productRepository.getProductById(productId);
        }
        return productRepository.getProductById(productId);
    }

    public LiveData<Boolean> getIsLoading() {
        return productRepository.getIsDetailLoading();
    }

    public LiveData<String> getErrorMessage() {
        return productRepository.getDetailErrorMessage();
    }

    public void refreshProduct(int productId) {
        currentProductId = productId;
        productRepository.getProductById(productId);
    }
    
    public LiveData<Boolean> getIsAddingToCart() {
        return cartRepository.getIsLoading();
    }
    
    public LiveData<Boolean> getAddToCartSuccess() {
        return addToCartSuccess;
    }
    
    public LiveData<String> getAddToCartError() {
        return cartRepository.getErrorMessage();
    }
    
    public void addToCart(int productId, int quantity, double price) {
        addToCartSuccess.setValue(false);
        
        cartRepository.addToCart(userId, productId, quantity, price).observeForever(cart -> {
            if (cart != null) {
                addToCartSuccess.setValue(true);
            }
        });
    }
} 