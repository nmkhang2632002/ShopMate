package com.example.shopmate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.shopmate.view.CartFragment;
import com.example.shopmate.view.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                setCurrentFragment(new HomeFragment()); // Replace with ProfileFragment when available
            }
            return true;
        });
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.flFragment, fragment)
                .commit();
    }
}