package com.example.shopmate.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.shopmate.R;
import com.example.shopmate.util.AuthManager;
import com.example.shopmate.ui.fragments.CartFragment;
import com.example.shopmate.ui.fragments.HomeFragment;
import com.example.shopmate.ui.fragments.ProfileFragment;
import com.example.shopmate.viewmodel.AuthViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private AuthManager authManager;

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

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        Fragment homeFragment = new HomeFragment();

        // Set initial fragment
        if (savedInstanceState == null) {
            setCurrentFragment(homeFragment);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                setCurrentFragment(new HomeFragment());
            } else if (id == R.id.nav_search) {
                setCurrentFragment(new HomeFragment()); // Replace with SearchFragment when available
            } else if (id == R.id.nav_cart) {
                setCurrentFragment(new CartFragment());
            } else if (id == R.id.nav_profile) {
                setCurrentFragment(new ProfileFragment());
            }
            return true;
        });
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

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
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

    @Override
    protected void onResume() {
        super.onResume();
        // Check authentication status when activity resumes
        if (!authManager.isLoggedIn()) {
            redirectToLogin();
        }
    }
}