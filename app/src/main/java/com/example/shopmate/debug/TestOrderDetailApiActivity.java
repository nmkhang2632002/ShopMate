package com.example.shopmate.debug;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.shopmate.R;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.OrderDetailResponse;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.data.network.VNPayApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestOrderDetailApiActivity extends AppCompatActivity {
    
    private TextView resultText;
    private VNPayApi vnPayApi;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Reuse existing layout
        
        resultText = new TextView(this);
        setContentView(resultText);
        
        vnPayApi = RetrofitClient.getInstance().create(VNPayApi.class);
        
        testOrderDetailApi();
    }
    
    private void testOrderDetailApi() {
        resultText.setText("Testing API...");
        
        vnPayApi.getOrderDetail(78).enqueue(new Callback<ApiResponse<OrderDetailResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<OrderDetailResponse>> call, Response<ApiResponse<OrderDetailResponse>> response) {
                StringBuilder result = new StringBuilder();
                result.append("=== API Test Results ===\n");
                result.append("Response successful: ").append(response.isSuccessful()).append("\n");
                result.append("Response code: ").append(response.code()).append("\n");
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<OrderDetailResponse> apiResponse = response.body();
                    result.append("API response status: ").append(apiResponse.getStatus()).append("\n");
                    result.append("API response message: ").append(apiResponse.getMessage()).append("\n");
                    result.append("API response successful: ").append(apiResponse.isSuccessful()).append("\n");
                    
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        OrderDetailResponse orderDetail = apiResponse.getData();
                        result.append("\n=== Order Detail ===\n");
                        result.append("Order ID: ").append(orderDetail.getId()).append("\n");
                        result.append("Payment Method: ").append(orderDetail.getPaymentMethod()).append("\n");
                        result.append("Order Status: ").append(orderDetail.getOrderStatus()).append("\n");
                        result.append("Username: ").append(orderDetail.getUsername()).append("\n");
                        result.append("Phone: ").append(orderDetail.getPhoneNumber()).append("\n");
                        result.append("Total Amount: ").append(orderDetail.getTotalAmount()).append("\n");
                        
                        result.append("\n=== Cart Items ===\n");
                        if (orderDetail.getCartItems() != null) {
                            result.append("Cart Items Count: ").append(orderDetail.getCartItems().size()).append("\n");
                            for (int i = 0; i < orderDetail.getCartItems().size(); i++) {
                                var item = orderDetail.getCartItems().get(i);
                                result.append("Item ").append(i + 1).append(":\n");
                                result.append("  - Name: ").append(item.getProductName()).append("\n");
                                result.append("  - Price: ").append(item.getPrice()).append("\n");
                                result.append("  - Quantity: ").append(item.getQuantity()).append("\n");
                                result.append("  - Subtotal: ").append(item.getSubtotal()).append("\n");
                                result.append("  - Image: ").append(item.getProductImage()).append("\n");
                            }
                        } else {
                            result.append("Cart Items: NULL\n");
                        }
                    } else {
                        result.append("OrderDetail data is null\n");
                    }
                } else {
                    result.append("Response body is null or unsuccessful\n");
                    if (response.errorBody() != null) {
                        try {
                            result.append("Error: ").append(response.errorBody().string()).append("\n");
                        } catch (Exception e) {
                            result.append("Error reading error body: ").append(e.getMessage()).append("\n");
                        }
                    }
                }
                
                resultText.setText(result.toString());
            }
            
            @Override
            public void onFailure(Call<ApiResponse<OrderDetailResponse>> call, Throwable t) {
                resultText.setText("API call failed: " + t.getMessage());
            }
        });
    }
}
