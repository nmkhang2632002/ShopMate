package com.example.shopmate.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.User;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.data.network.UserApi;
import com.example.shopmate.ui.activities.AdminActivity;
import com.example.shopmate.ui.activities.MainActivity;
import com.example.shopmate.util.AuthManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Test Activity để verify chức năng chuyển đổi giữa Admin Dashboard và Home
 * dựa trên role của user
 */
public class TestAdminSwitchActivity extends AppCompatActivity {

    private static final String TAG = "TestAdminSwitch";
    private TextView tvUserInfo;
    private Button btnTestGetUser;
    private Button btnGoToHome;
    private Button btnGoToAdmin;
    private AuthManager authManager;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_admin_switch);

        authManager = AuthManager.getInstance(this);
        userApi = RetrofitClient.getInstance().create(UserApi.class);

        initViews();
        setupClickListeners();
        
        // Auto test user role on create
        testGetCurrentUser();
    }

    private void initViews() {
        tvUserInfo = findViewById(R.id.tvUserInfo);
        btnTestGetUser = findViewById(R.id.btnTestGetUser);
        btnGoToHome = findViewById(R.id.btnGoToHome);
        btnGoToAdmin = findViewById(R.id.btnGoToAdmin);
    }

    private void setupClickListeners() {
        btnTestGetUser.setOnClickListener(v -> testGetCurrentUser());
        
        btnGoToHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });
        
        btnGoToAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
        });
    }

    private void testGetCurrentUser() {
        int userId = authManager.getUserId();
        if (userId == -1) {
            tvUserInfo.setText("User not logged in");
            return;
        }

        tvUserInfo.setText("Loading user info...");
        
        userApi.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();
                        
                        String userInfo = "✅ API Success!\n\n" +
                                "User ID: " + user.getId() + "\n" +
                                "Username: " + user.getUsername() + "\n" +
                                "Email: " + user.getEmail() + "\n" +
                                "Role: " + user.getRole() + "\n\n";
                        
                        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                            userInfo += "🔑 ADMIN - Có thể truy cập Admin Dashboard\n" +
                                       "✅ Admin FAB sẽ hiển thị trong MainActivity";
                            btnGoToAdmin.setEnabled(true);
                        } else {
                            userInfo += "👤 CUSTOMER - Chỉ có thể dùng chức năng khách hàng\n" +
                                       "❌ Admin FAB sẽ ẩn trong MainActivity";
                            btnGoToAdmin.setEnabled(false);
                        }
                        
                        tvUserInfo.setText(userInfo);
                        Log.d(TAG, "User info: " + userInfo);
                        
                    } else {
                        tvUserInfo.setText("❌ API Error: " + apiResponse.getMessage());
                    }
                } else {
                    tvUserInfo.setText("❌ HTTP Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                String error = "❌ Network Error: " + t.getMessage();
                tvUserInfo.setText(error);
                Log.e(TAG, error);
            }
        });
    }
}
