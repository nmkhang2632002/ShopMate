package com.example.shopmate.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.shopmate.data.model.Cart;
import com.example.shopmate.data.repository.CartRepository;
import com.example.shopmate.util.AuthManager;
import com.example.shopmate.util.BadgeUtils;
import com.example.shopmate.util.NotificationUtils;

public class CartViewModel extends AndroidViewModel {
    private final CartRepository cartRepository;
    private final AuthManager authManager;
    private MutableLiveData<Cart> cart = new MutableLiveData<>();
    private final Application application;
    private final Observer<Cart> badgeObserver;
    
    public CartViewModel(Application application) {
        super(application);
        this.application = application;
        cartRepository = CartRepository.getInstance();
        authManager = AuthManager.getInstance(application);
        
        // Tạo observer để cập nhật badge khi giỏ hàng thay đổi
        badgeObserver = this::updateBadgeCount;
        
        // Đăng ký observer
        cart.observeForever(badgeObserver);
        
        // Tải giỏ hàng
        loadCart();
    }
    
    /**
     * Cập nhật số lượng badge trên icon ứng dụng
     */
    private void updateBadgeCount(Cart cart) {
        if (cart != null && cart.getTotalItems() > 0) {
            int itemCount = cart.getTotalItems();
            
            // Cập nhật badge với số lượng sản phẩm trong giỏ hàng
            BadgeUtils.updateBadge(application, itemCount);
            
            // Hiển thị thông báo im lặng để hiển thị badge
            NotificationUtils.showBadgeNotification(application, itemCount);
        } else {
            // Xóa badge nếu giỏ hàng trống
            BadgeUtils.removeBadge(application);
            NotificationUtils.cancelBadgeNotification(application);
        }
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

    public void addToCart(int productId, int quantity) {
        int userId = authManager.getUserId();
        if (userId != -1) {
            cartRepository.addToCart(userId, productId, quantity).observeForever(updatedCart -> {
                if (updatedCart != null) {
                    cart.setValue(updatedCart);
                }
            });
        }
    }
    
    @Override
    protected void onCleared() {
        super.onCleared();
        // Hủy đăng ký observer để tránh memory leak
        cart.removeObserver(badgeObserver);
    }
} 
