package com.example.shopmate.debug;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.data.network.OrderApi;
import com.example.shopmate.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Test class để verify Admin Order Management APIs
 * Usage: Chạy activity này để test các API calls thực tế
 */
public class TestAdminOrderApiActivity extends AppCompatActivity {

    private static final String TAG = "TestAdminOrderApi";
    private OrderApi orderApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Sử dụng layout có sẵn

        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
        
        // Test các APIs
        testGetAllOrders();
        testGetOrdersByUserId();
        testGetOrderDetail();
    }

    /**
     * Test API: GET /v1/orders
     */
    private void testGetAllOrders() {
        Log.d(TAG, "Testing GET ALL ORDERS API...");
        
        orderApi.getAllOrders(0, 10).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Order>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        Log.d(TAG, "✅ GET ALL ORDERS SUCCESS!");
                        Log.d(TAG, "Orders count: " + apiResponse.getData().size());
                        
                        // Log chi tiết order đầu tiên
                        if (!apiResponse.getData().isEmpty()) {
                            Order firstOrder = apiResponse.getData().get(0);
                            Log.d(TAG, "First Order ID: " + firstOrder.getId());
                            Log.d(TAG, "Order Status: " + firstOrder.getOrderStatus());
                            Log.d(TAG, "Payment Method: " + firstOrder.getPaymentMethod());
                            
                            // Check cart items
                            if (firstOrder.getCartItems() != null) {
                                Log.d(TAG, "Cart Items count: " + firstOrder.getCartItems().size());
                            }
                        }
                    } else {
                        Log.e(TAG, "❌ API Error: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ Network Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Log.e(TAG, "❌ API Call Failed: " + t.getMessage());
            }
        });
    }

    /**
     * Test API: GET /v1/orders/{userId}
     */
    private void testGetOrdersByUserId() {
        Log.d(TAG, "Testing GET ORDERS BY USER ID API...");
        
        int testUserId = 1; // Test với user ID = 1
        
        orderApi.getOrdersByUserId(testUserId).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Order>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        Log.d(TAG, "✅ GET ORDERS BY USER ID SUCCESS!");
                        Log.d(TAG, "User " + testUserId + " has " + apiResponse.getData().size() + " orders");
                    } else {
                        Log.e(TAG, "❌ API Error: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ Network Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                Log.e(TAG, "❌ API Call Failed: " + t.getMessage());
            }
        });
    }

    /**
     * Test API: GET /v1/orders/detail/{orderId}
     */
    private void testGetOrderDetail() {
        Log.d(TAG, "Testing GET ORDER DETAIL API...");
        
        int testOrderId = 97; // Test với order ID = 97
        
        orderApi.getOrderDetail(testOrderId).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Order> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        Log.d(TAG, "✅ GET ORDER DETAIL SUCCESS!");
                        Order order = apiResponse.getData();
                        Log.d(TAG, "Order ID: " + order.getId());
                        Log.d(TAG, "User ID: " + order.getUserId());
                        Log.d(TAG, "Status: " + order.getOrderStatus());
                        
                        // Check cart items detail
                        if (order.getCartItems() != null && !order.getCartItems().isEmpty()) {
                            Log.d(TAG, "Cart Items Detail:");
                            order.getCartItems().forEach(item -> {
                                Log.d(TAG, "  - " + item.getProductName() + " x" + item.getQuantity() + " = " + item.getSubtotal());
                            });
                        }
                        
                        // Check payments
                        if (order.getPayments() != null && !order.getPayments().isEmpty()) {
                            Log.d(TAG, "Payment Status: " + order.getPayments().get(0).getPaymentStatus());
                            Log.d(TAG, "Payment Amount: " + order.getPayments().get(0).getAmount());
                        }
                        
                    } else {
                        Log.e(TAG, "❌ API Error: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e(TAG, "❌ Network Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                Log.e(TAG, "❌ API Call Failed: " + t.getMessage());
            }
        });
    }
}
