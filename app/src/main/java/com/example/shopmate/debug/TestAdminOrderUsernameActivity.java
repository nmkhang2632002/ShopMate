package com.example.shopmate.debug;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopmate.R;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.viewmodel.AdminOrderViewModel;

/**
 * Test activity để verify Admin Order username fetching
 * Kiểm tra xem username có được load đúng từ UserApi không
 */
public class TestAdminOrderUsernameActivity extends AppCompatActivity {

    private static final String TAG = "TestAdminUsername";
    private AdminOrderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(AdminOrderViewModel.class);
        
        setupObservers();
        testLoadAllOrdersWithUsernames();
    }

    private void setupObservers() {
        // Observe orders list
        viewModel.getOrders().observe(this, orders -> {
            if (orders != null && !orders.isEmpty()) {
                Log.d(TAG, "✅ Orders loaded with usernames! Count: " + orders.size());
                
                // Test username data for each order
                for (int i = 0; i < Math.min(orders.size(), 5); i++) { // Test first 5 orders
                    Order order = orders.get(i);
                    Log.d(TAG, "=== ORDER #" + order.getId() + " ===");
                    Log.d(TAG, "User ID: " + order.getUserId());
                    Log.d(TAG, "Customer Name: " + order.getUserName());
                    Log.d(TAG, "Total Amount: " + order.getTotalAmount());
                    Log.d(TAG, "Total Items: " + order.getTotalItems());
                    Log.d(TAG, "Status: " + order.getOrderStatus());
                    
                    // Check if username is real or fallback
                    if (order.getUserName() != null) {
                        if (order.getUserName().startsWith("Customer #")) {
                            Log.w(TAG, "⚠️ Using fallback name: " + order.getUserName());
                        } else if (order.getUserName().equals("Loading...")) {
                            Log.w(TAG, "⏳ Still loading username...");
                        } else {
                            Log.d(TAG, "✅ Real username loaded: " + order.getUserName());
                        }
                    }
                    
                    Log.d(TAG, "========================");
                }
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

        // Observe single order detail
        viewModel.getOrderDetail().observe(this, order -> {
            if (order != null) {
                Log.d(TAG, "📄 Order Detail Loaded:");
                Log.d(TAG, "Order ID: " + order.getId());
                Log.d(TAG, "Customer Name: " + order.getUserName());
                Log.d(TAG, "User ID: " + order.getUserId());
                
                if (order.getUserName() != null && !order.getUserName().startsWith("Customer #")) {
                    Log.d(TAG, "✅ Real username in detail: " + order.getUserName());
                } else {
                    Log.w(TAG, "⚠️ Fallback name in detail: " + order.getUserName());
                }
            }
        });
    }

    private void testLoadAllOrdersWithUsernames() {
        Log.d(TAG, "🚀 Testing loadAllOrders with username fetching...");
        viewModel.loadAllOrders();
        
        // Test single order detail after 3 seconds
        new android.os.Handler().postDelayed(() -> {
            Log.d(TAG, "🔍 Testing single order detail...");
            viewModel.getOrderDetail(97); // Test with specific order ID
        }, 3000);
    }
}
