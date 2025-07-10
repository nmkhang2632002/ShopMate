package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.model.Cart;
import com.example.shopmate.repository.CartRepository;

public class CartViewModel extends ViewModel {
    private final CartRepository cartRepository;
    private LiveData<Cart> cart;
    
    // Current user ID (in a real app, this would come from user authentication)
    private final int userId = 1;
    
    public CartViewModel() {
        cartRepository = CartRepository.getInstance();
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
        cart = cartRepository.getCart(userId);
    }
    
    public void updateCartItemQuantity(int itemId, int quantity) {
        cart = cartRepository.updateCartItemQuantity(userId, itemId, quantity);
    }
    
    public void removeCartItem(int itemId) {
        cart = cartRepository.removeCartItem(userId, itemId);
    }
} 