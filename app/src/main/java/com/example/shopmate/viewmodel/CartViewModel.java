package com.example.shopmate.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.Cart;
import com.example.shopmate.data.repository.CartRepository;
import com.example.shopmate.util.AuthManager;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private MutableLiveData<Cart> cart = new MutableLiveData<>();
    
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
            cartRepository.getCart(userId).observeForever(cartData -> {
                cart.setValue(cartData);
            });
        }
    }
    
    public void updateCartItemQuantity(int itemId, int quantity) {
        int userId = authManager.getUserId();
        if (userId != -1) {
            cartRepository.updateCartItemQuantity(userId, itemId, quantity).observeForever(updatedCart -> {
                if (updatedCart != null) {
                    cart.setValue(updatedCart);
                }
            });
        }
    }
    
    public void removeCartItem(int itemId) {
        int userId = authManager.getUserId();
        if (userId != -1) {
            cartRepository.removeCartItem(userId, itemId).observeForever(updatedCart -> {
                if (updatedCart != null) {
                    cart.setValue(updatedCart);
                }
            });
        }
    }

    public void clearCart() {
        int userId = authManager.getUserId();
        if (userId != -1) {
            cartRepository.clearCart(userId).observeForever(updatedCart -> {
                if (updatedCart != null) {
                    cart.setValue(updatedCart);
                }
            });
        }
    }

} 
