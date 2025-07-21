package com.example.shopmate.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.Order;
import com.example.shopmate.data.repository.OrderRepository;
import com.example.shopmate.util.AuthManager;

public class OrderViewModel extends AndroidViewModel {
    
    private final OrderRepository orderRepository;
    private final AuthManager authManager;
    
    public OrderViewModel(Application application) {
        super(application);
        orderRepository = new OrderRepository();
        authManager = AuthManager.getInstance(application);
    }
    
    public LiveData<Order> createOrder(String paymentMethod, String billingAddress) {
        int userId = authManager.getUserId();
        return orderRepository.createOrder(userId, paymentMethod, billingAddress);
    }

    public LiveData<String> createVNPayOrder(String billingAddress) {
        int userId = authManager.getUserId();
        return orderRepository.createVNPayOrder(userId, billingAddress);
    }
    
    public LiveData<Boolean> getIsLoading() {
        return orderRepository.getIsLoading();
    }
    
    public LiveData<String> getErrorMessage() {
        return orderRepository.getErrorMessage();
    }
}

