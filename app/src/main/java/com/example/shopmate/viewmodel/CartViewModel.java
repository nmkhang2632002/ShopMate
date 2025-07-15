package com.example.shopmate.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.shopmate.data.model.Cart;
import com.example.shopmate.data.repository.CartRepository;
import com.example.shopmate.util.AuthManager;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private LiveData<Cart> cart;
    
    public CartViewModel(Application application) {
        super(application);
        cartRepository = CartRepository.getInstance();
        authManager = AuthManager.getInstance(application);
        loadCart();
    }
    
    public LiveData<Cart> getCart() {
        return cart;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return cartRepository.getIsLoading();
    }
    
    public LiveData<String> getErrorMessage() {
        return cartRepository.getErrorMessage();
    }
    
    public void loadCart() {
        int userId = authManager.getUserId();
        if (userId != -1) {
            cart = cartRepository.getCart(userId);
        }
    }
    
    public void updateCartItemQuantity(int itemId, int quantity) {
        int userId = authManager.getUserId();
        if (userId != -1) {
            cart = cartRepository.updateCartItemQuantity(userId, itemId, quantity);
        }
    }
    
    public void removeCartItem(int itemId) {
        int userId = authManager.getUserId();
        if (userId != -1) {
            cart = cartRepository.removeCartItem(userId, itemId);
        }
    }
} 