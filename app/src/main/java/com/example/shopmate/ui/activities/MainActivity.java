package com.example.shopmate.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopmate.R;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.User;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.data.network.UserApi;
import com.example.shopmate.util.AuthManager;
import com.example.shopmate.ui.fragments.CartFragment;
import com.example.shopmate.ui.fragments.ChatFragment;
import com.example.shopmate.ui.fragments.HomeFragment;
import com.example.shopmate.ui.fragments.MapFragment;
import com.example.shopmate.ui.fragments.ProfileFragment;
import com.example.shopmate.ui.fragments.OrderSuccessFragment;
import com.example.shopmate.ui.fragments.PaymentFailedFragment;
import com.example.shopmate.viewmodel.CartViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.shopmate.util.BadgeUtils;
import com.example.shopmate.util.NotificationUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private AuthManager authManager;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fabAdmin;
    private CartViewModel cartViewModel;
    private UserApi userApi;
    
    // Permission request launcher
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize AuthManager
        authManager = AuthManager.getInstance(this);
        
        // Check if user is logged in
        if (!authManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }
        
        setContentView(R.layout.activity_main);

        // Initialize ViewModels and APIs
        cartViewModel = new ViewModelProvider(this).get(CartViewModel.class);
        userApi = RetrofitClient.getInstance().create(UserApi.class);
        
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fabAdmin = findViewById(R.id.fabAdmin);

        Fragment homeFragment = new HomeFragment();

        // Set initial fragment
        if (savedInstanceState == null) {
            setCurrentFragment(homeFragment);
        }

        // Check if user is admin and show/hide admin FAB
        checkUserRoleAndShowAdminFAB();

        // Setup FAB click listener
        fabAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AdminActivity.class);
            startActivity(intent);
        });

        // Handle payment result navigation
        handlePaymentResultNavigation();

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                setCurrentFragment(new HomeFragment());
            } else if (id == R.id.nav_map) {
                setCurrentFragment(new MapFragment());
            } else if (id == R.id.nav_cart) {
                setCurrentFragment(new CartFragment());
            } else if (id == R.id.nav_chat) {
                setCurrentFragment(new ChatFragment());
            } else if (id == R.id.nav_profile) {
                setCurrentFragment(new ProfileFragment());
            }
            return true;
        });
        
        // Initialize permission launcher
        requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (!isGranted) {
                    Toast.makeText(this, 
                        "Badge notification permission denied. Some features may not work properly.", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        );
        
        // Check and request notification permission for Android 13+
        checkNotificationPermission();
    }
    
    private void checkNotificationPermission() {
        // For Android 13+ (API level 33+), check POST_NOTIFICATIONS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                    != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }

    private void redirectToLogin() {
        // Xóa badge và thông báo trước khi chuyển đến màn hình đăng nhập
        BadgeUtils.removeBadge(this);
        NotificationUtils.cancelBadgeNotification(this);
        BadgeUtils.clearSavedBadgeCount(this);
        
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void logout() {
        // Xóa badge và thông báo khi đăng xuất
        BadgeUtils.forceRemoveBadge(this);
        NotificationUtils.cancelBadgeNotification(this);
        
        authManager.logout();
        redirectToLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check authentication status when activity resumes
        if (!authManager.isLoggedIn()) {
            redirectToLogin();
        } else {
            // Refresh cart data when activity resumes
            cartViewModel.loadCart();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        
        // Kiểm tra nếu đang đăng xuất, xóa badge
        if (!authManager.isLoggedIn()) {
            BadgeUtils.forceRemoveBadge(this);
            NotificationUtils.cancelBadgeNotification(this);
        }
    }

    public void navigateToHome() {
        // Clear back stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        // Set home fragment
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, homeFragment)
                .commit();
        
        // Update bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }
    
    public void navigateToChat() {
        // Clear back stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        // Set chat fragment
        ChatFragment chatFragment = new ChatFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, chatFragment)
                .commit();
        
        // Update bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_chat);
    }
    
    public void navigateToMap() {
        // Clear back stack
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        
        // Set map fragment
        MapFragment mapFragment = new MapFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, mapFragment)
                .commit();
        
        // Update bottom navigation
        bottomNavigationView.setSelectedItemId(R.id.nav_map);
    }

    private void handlePaymentResultNavigation() {
        Intent intent = getIntent();

        if (intent.getBooleanExtra("show_order_success", false)) {
            String orderId = intent.getStringExtra("order_id");
            String transactionId = intent.getStringExtra("transaction_id");
            String paymentMethod = intent.getStringExtra("payment_method");

            // Show order success fragment for VNPay payment
            if ("VNPay".equals(paymentMethod)) {
                // Lấy thông tin bổ sung từ intent
                String username = intent.getStringExtra("username");
                String phoneNumber = intent.getStringExtra("phone_number");
                String billingAddress = intent.getStringExtra("billing_address");
                String orderStatus = intent.getStringExtra("order_status");
                String totalAmount = intent.getStringExtra("total_amount");

                OrderSuccessFragment successFragment = OrderSuccessFragment.newInstanceForVNPay(
                        orderId, transactionId, username, phoneNumber, billingAddress, orderStatus, totalAmount);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, successFragment)
                        .commit();

                // Update bottom navigation to home
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            } else {
                // Fallback for other payment methods
                Toast.makeText(this, "Order #" + orderId + " completed successfully!", Toast.LENGTH_LONG).show();
                navigateToHome();
            }

        } else if (intent.getBooleanExtra("show_payment_failed", false)) {
            String orderId = intent.getStringExtra("order_id");
            String errorMessage = intent.getStringExtra("error_message");

            // Show payment failed fragment
            PaymentFailedFragment failedFragment = PaymentFailedFragment.newInstance(orderId, errorMessage);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flFragment, failedFragment)
                    .commit();

            // Update bottom navigation to cart
            bottomNavigationView.setSelectedItemId(R.id.nav_cart);

        } else if (intent.getBooleanExtra("show_checkout", false)) {
            // Navigate back to cart/checkout
            setCurrentFragment(new CartFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_cart);
        }
    }

    /**
     * Check if current user is admin and show/hide admin FAB accordingly
     */
    private void checkUserRoleAndShowAdminFAB() {
        int userId = authManager.getUserId();
        if (userId == -1) {
            // User not logged in, hide FAB
            fabAdmin.setVisibility(View.GONE);
            return;
        }

        userApi.getUserById(userId).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<User> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        User user = apiResponse.getData();
                        String role = user.getRole();
                        
                        Log.d(TAG, "User role: " + role);
                        
                        // Show FAB only if user is ADMIN
                        if ("ADMIN".equalsIgnoreCase(role)) {
                            fabAdmin.setVisibility(View.VISIBLE);
                            Log.d(TAG, "Admin FAB shown for user: " + user.getUsername());
                        } else {
                            fabAdmin.setVisibility(View.GONE);
                            Log.d(TAG, "Admin FAB hidden for non-admin user: " + user.getUsername());
                        }
                    } else {
                        Log.w(TAG, "Failed to get user data: " + apiResponse.getMessage());
                        fabAdmin.setVisibility(View.GONE);
                    }
                } else {
                    Log.w(TAG, "Failed to fetch user role: " + response.code());
                    fabAdmin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                Log.e(TAG, "Error fetching user role: " + t.getMessage());
                fabAdmin.setVisibility(View.GONE);
            }
        });
    }
    
    public CartViewModel getCartViewModel() {
        return cartViewModel;
    }
}
