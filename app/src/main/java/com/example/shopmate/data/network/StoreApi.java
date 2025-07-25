package com.example.shopmate.data.network;

import com.example.shopmate.data.model.StoreLocation;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StoreApi {
    
    @GET("store/list")
    Call<List<StoreLocation>> getStoreLocations();
} 