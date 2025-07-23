package com.example.shopmate.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.repository.ProductRepository;
import com.example.shopmate.data.repository.CartRepository;
import com.example.shopmate.util.AuthManager;

public class ProductDetailViewModel extends AndroidViewModel {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private int currentProductId = -1;
    private final MutableLiveData<Boolean> addToCartSuccess = new MutableLiveData<>(false);

    public ProductDetailViewModel(Application application) {
        super(application);
        productRepository = new ProductRepository();
        cartRepository = CartRepository.getInstance();
        authManager = AuthManager.getInstance(application);
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
    
    public void addToCart(int productId, int quantity) {
        addToCartSuccess.setValue(false);
        
        int userId = authManager.getUserId();
        if (userId != -1) {
            cartRepository.addToCart(userId, productId,quantity).observeForever(cart -> {
                if (cart != null) {
                    addToCartSuccess.setValue(true);
                }
            });
        } else {
            // Handle case where user is not logged in
            MutableLiveData<String> errorMessage = (MutableLiveData<String>) cartRepository.getErrorMessage();
            errorMessage.setValue("Please login to add items to cart");
        }
    }
} 