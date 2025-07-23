package com.example.shopmate.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.example.shopmate.R;
import com.example.shopmate.ui.fragments.OrderDetailFragment;

public class TestOrderDetailActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Reuse the main activity layout
        
        // Load OrderDetailFragment with order ID 78
        OrderDetailFragment fragment = OrderDetailFragment.newInstance(78);
        
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, fragment);
        transaction.commit();
    }
}
