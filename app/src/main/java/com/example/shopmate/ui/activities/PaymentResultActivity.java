package com.example.shopmate.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopmate.R;
import com.example.shopmate.ui.fragments.OrderSuccessFragment;
import com.example.shopmate.ui.fragments.PaymentFailedFragment;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.OrderDetail;
import com.example.shopmate.data.model.User;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.data.network.VNPayApi;
import com.example.shopmate.data.network.UserApi;
import com.example.shopmate.util.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentResultActivity extends AppCompatActivity {

    private VNPayApi vnPayApi;
    private UserApi userApi;
    private AuthManager authManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        // Initialize APIs
        vnPayApi = RetrofitClient.getInstance().create(VNPayApi.class);
        userApi = RetrofitClient.getInstance().create(UserApi.class);
        authManager = AuthManager.getInstance(this);

        handlePaymentResult();
    }
    
    private void handlePaymentResult() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        
        if (data != null) {
            String status = data.getQueryParameter("status");
            String orderId = data.getQueryParameter("orderId");
            String transactionId = data.getQueryParameter("transactionId");
            String error = data.getQueryParameter("error");
            
            switch (status != null ? status : "") {
                case "success":
                    handlePaymentSuccess(orderId, transactionId);
                    break;
                case "failed":
                    handlePaymentFailed(orderId, error);
                    break;
                case "error":
                    handlePaymentError(orderId, error);
                    break;
                default:
                    handleUnknownResult();
                    break;
            }
        } else {
            handleUnknownResult();
        }
    }
    
    private void handlePaymentSuccess(String orderId, String transactionId) {
        Toast.makeText(this, "Payment successful! Order #" + orderId, Toast.LENGTH_LONG).show();

        // Gọi API để lấy thông tin order đầy đủ
        fetchOrderDetailAndNavigate(orderId, transactionId);
    }

    private void fetchOrderDetailAndNavigate(String orderId, String transactionId) {
        try {
            int orderIdInt = Integer.parseInt(orderId);

            // Gọi API lấy order detail
            vnPayApi.getOrderDetail(orderIdInt).enqueue(new Callback<ApiResponse<OrderDetail>>() {
                @Override
                public void onResponse(Call<ApiResponse<OrderDetail>> call, Response<ApiResponse<OrderDetail>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccessful()) {
                        OrderDetail orderDetail = response.body().getData();

                        // Lấy thông tin user
                        fetchUserInfoAndNavigate(orderDetail, transactionId);
                    } else {
                        // Fallback với thông tin cơ bản
                        navigateWithBasicInfo(orderId, transactionId);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<OrderDetail>> call, Throwable t) {
                    // Fallback với thông tin cơ bản
                    navigateWithBasicInfo(orderId, transactionId);
                }
            });
        } catch (NumberFormatException e) {
            navigateWithBasicInfo(orderId, transactionId);
        }
    }

    private void fetchUserInfoAndNavigate(OrderDetail orderDetail, String transactionId) {
        int userId = authManager.getUserId();

        userApi.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                String username = "User";
                String phoneNumber = "N/A";
                String address = "N/A";

                if (response.isSuccessful() && response.body() != null && response.body().isSuccessful()) {
                    User user = response.body().getData();
                    username = user.getUsername();
                    phoneNumber = user.getPhoneNumber();
                    address = user.getAddress();
                }

                // Navigate với thông tin đầy đủ
                navigateToMainActivity(orderDetail, transactionId, username, phoneNumber, address);
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Navigate với thông tin order nhưng user info mặc định
                navigateToMainActivity(orderDetail, transactionId, "User", "N/A", "N/A");
            }
        });
    }

    private void navigateToMainActivity(OrderDetail orderDetail, String transactionId, String username, String phoneNumber, String address) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("show_order_success", true);
        intent.putExtra("order_id", String.valueOf(orderDetail.getId()));
        intent.putExtra("transaction_id", transactionId);
        intent.putExtra("payment_method", "VNPay");
        intent.putExtra("username", username);
        intent.putExtra("phone_number", phoneNumber);
        intent.putExtra("billing_address", orderDetail.getBillingAddress());
        intent.putExtra("order_status", orderDetail.getOrderStatus());
        intent.putExtra("total_amount", orderDetail.getTotalAmount() != null ? orderDetail.getTotalAmount().toString() : "0");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateWithBasicInfo(String orderId, String transactionId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("show_order_success", true);
        intent.putExtra("order_id", orderId);
        intent.putExtra("transaction_id", transactionId);
        intent.putExtra("payment_method", "VNPay");
        intent.putExtra("username", "User");
        intent.putExtra("phone_number", "N/A");
        intent.putExtra("billing_address", "N/A");
        intent.putExtra("order_status", "Processing");
        intent.putExtra("total_amount", "0");

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void handlePaymentFailed(String orderId, String error) {
        String message = "Payment failed";
        if (error != null) {
            message += ": " + error;
        }

        // Navigate to MainActivity and show PaymentFailedFragment
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("show_payment_failed", true);
        intent.putExtra("order_id", orderId);
        intent.putExtra("error_message", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void handlePaymentError(String orderId, String error) {
        String message = "Payment error";
        if (error != null) {
            message += ": " + error;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        
        // Navigate back to home
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    
    private void handleUnknownResult() {
        Toast.makeText(this, "Unknown payment result", Toast.LENGTH_SHORT).show();
        
        // Navigate back to home
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
