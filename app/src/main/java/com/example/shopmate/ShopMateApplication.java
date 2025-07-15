package com.example.shopmate;

import android.app.Application;

import com.example.shopmate.data.network.RetrofitClient;

public class ShopMateApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize RetrofitClient with application context
        RetrofitClient.initialize(this);
    }
} 