package com.example.shopmate.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopmate.R;
import com.example.shopmate.util.AuthManager;
import com.example.shopmate.ui.fragments.AdminProductsFragment;
import com.example.shopmate.ui.fragments.AdminCategoriesFragment;
import com.example.shopmate.ui.fragments.AdminOrdersFragment;
import com.example.shopmate.ui.fragments.AdminProfileFragment;
import com.example.shopmate.ui.fragments.AdminChatFragment;
import com.example.shopmate.viewmodel.AdminChatViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminActivity extends AppCompatActivity {

    private AuthManager authManager;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private AdminChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize AuthManager
        authManager = AuthManager.getInstance(this);

        // Check if user is admin
        if (!authManager.isLoggedIn() || !isAdmin()) {
            redirectToLogin();
            return;
        }

        setContentView(R.layout.activity_admin);

        // Initialize ViewModels
        chatViewModel = new ViewModelProvider(this).get(AdminChatViewModel.class);

        initViews();
        setupToolbar();
        setupBottomNavigation();

        // Set initial fragment - Orders Management first
        if (savedInstanceState == null) {
            setCurrentFragment(new AdminOrdersFragment());
            bottomNavigationView.setSelectedItemId(R.id.nav_admin_orders);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Orders Management");
            }
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        
        // Setup Home FAB
        findViewById(R.id.fabHome).setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Admin Dashboard");
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment = null;
            String title = "Admin Dashboard";

            if (id == R.id.nav_admin_products) {
                selectedFragment = new AdminProductsFragment();
                title = "Products Management";
            } else if (id == R.id.nav_admin_categories) {
                selectedFragment = new AdminCategoriesFragment();
                title = "Categories Management";
            } else if (id == R.id.nav_admin_orders) {
                selectedFragment = new AdminOrdersFragment();
                title = "Orders Management";
            } else if (id == R.id.nav_admin_chat) {
                selectedFragment = new AdminChatFragment();
                title = "Customer Chat";
            } else if (id == R.id.nav_admin_profile) {
                selectedFragment = new AdminProfileFragment();
                title = "Admin Profile";
            }

            if (selectedFragment != null) {
                setCurrentFragment(selectedFragment);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
            }
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Don't inflate admin menu since we removed all items
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // No menu items to handle anymore
        return super.onOptionsItemSelected(item);
    }

    public void setCurrentFragment(Fragment fragment) {
        if (fragment != null && !isFinishing() && !isDestroyed()) {
            try {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.flFragment, fragment)
                        .commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void logout() {
        authManager.logout();
        redirectToLogin();
    }

    private void navigateToMainApp() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check authentication status when activity resumes
        if (!authManager.isLoggedIn() || !isAdmin()) {
            redirectToLogin();
        }
    }

    @Override
    protected void onDestroy() {
        // Clear any pending tasks to prevent memory leaks
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(null);
        }
        super.onDestroy();
    }

    private boolean isAdmin() {
        // Implement your logic to check if the user is an admin
        // For example, you can check a specific user ID or a flag in the user's data
        return authManager.getCurrentUser() != null && authManager.getCurrentUser().getId() == 40;
    }
}
