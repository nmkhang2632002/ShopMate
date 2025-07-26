package com.example.shopmate.debug;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.viewmodel.AdminOrderViewModel;

/**
 * Test activity để verify Admin Order data processing
 * Kiểm tra xem total amount, items count, customer name có hiển thị đúng không
 */
public class TestAdminOrderDataActivity extends AppCompatActivity {

    private static final String TAG = "TestAdminOrderData";
    private AdminOrderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(AdminOrderViewModel.class);
        
        setupObservers();
        testLoadAllOrders();
    }

    private void setupObservers() {
        // Observe orders list
        viewModel.getOrders().observe(this, orders -> {
            if (orders != null && !orders.isEmpty()) {
                Log.d(TAG, "✅ Orders loaded successfully! Count: " + orders.size());
                
                // Test first order data
                Order firstOrder = orders.get(0);
                Log.d(TAG, "=== FIRST ORDER DATA ===");
                Log.d(TAG, "Order ID: " + firstOrder.getId());
                Log.d(TAG, "Customer Name: " + firstOrder.getUserName());
                Log.d(TAG, "Total Amount: " + firstOrder.getTotalAmount());
                Log.d(TAG, "Total Items: " + firstOrder.getTotalItems());
                Log.d(TAG, "Order Status: " + firstOrder.getOrderStatus());
                Log.d(TAG, "Payment Method: " + firstOrder.getPaymentMethod());
                Log.d(TAG, "Billing Address: " + firstOrder.getBillingAddress());
                
                // Test cart items
                if (firstOrder.getCartItems() != null) {
                    Log.d(TAG, "Cart Items Count: " + firstOrder.getCartItems().size());
                    firstOrder.getCartItems().forEach(item -> {
                        Log.d(TAG, "  - " + item.getProductName() + 
                                " (ID: " + item.getProductID() + ")" +
                                " x" + item.getQuantity() + 
                                " = " + (item.getSubtotal() != null ? item.getSubtotal() : "0"));
                    });
                } else {
                    Log.w(TAG, "❌ No cart items found!");
                }
                
                // Test payments
                if (firstOrder.getPayments() != null && !firstOrder.getPayments().isEmpty()) {
                    Log.d(TAG, "Payment Amount: " + firstOrder.getPayments().get(0).getAmount());
                    Log.d(TAG, "Payment Status: " + firstOrder.getPayments().get(0).getPaymentStatus());
                } else {
                    Log.w(TAG, "❌ No payment info found!");
                }
                
                Log.d(TAG, "========================");
            } else {
                Log.w(TAG, "❌ No orders loaded or empty list");
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(this, isLoading -> {
            Log.d(TAG, "Loading state: " + isLoading);
        });

        // Observe errors
        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "❌ Error: " + error);
            }
        });
    }

    private void testLoadAllOrders() {
        Log.d(TAG, "🚀 Testing loadAllOrders()...");
        viewModel.loadAllOrders();
    }
}
