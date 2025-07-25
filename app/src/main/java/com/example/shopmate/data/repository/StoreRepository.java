package com.example.shopmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.StoreLocation;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.data.network.StoreApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;
public class StoreRepository {
    private final StoreApi storeApi;
    private final MutableLiveData<List<StoreLocation>> storeLocations = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public StoreRepository() {
        storeApi = RetrofitClient.getInstance().create(StoreApi.class);
    }

    public void fetchStoreLocations() {
        isLoading.setValue(true);
        
        storeApi.getStoreLocations().enqueue(new Callback<List<StoreLocation>>() {
            @Override
            public void onResponse(Call<List<StoreLocation>> call, Response<List<StoreLocation>> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    storeLocations.setValue(response.body());
                } else {
                    // If API call fails, use sample data
                    storeLocations.setValue(getSampleStoreLocations());
                    errorMessage.setValue("Failed to load store locations from server. Using sample data.");
                }
            }

            @Override
            public void onFailure(Call<List<StoreLocation>> call, Throwable t) {
                isLoading.setValue(false);
                // If API call fails, use sample data
                storeLocations.setValue(getSampleStoreLocations());
                errorMessage.setValue("Network error: " + t.getMessage() + ". Using sample data.");
            }
        });
    }
    
    // Fallback sample data
    private List<StoreLocation> getSampleStoreLocations() {
        List<StoreLocation> sampleLocations = new ArrayList<>();
        sampleLocations.add(new StoreLocation(1, 10.762622, 106.660172, "ShopMate Store - District 1", "123 Nguyen Hue, District 1, HCMC"));
        sampleLocations.add(new StoreLocation(2, 10.780230, 106.699053, "ShopMate Store - District 2", "456 Thao Dien, District 2, HCMC"));
        sampleLocations.add(new StoreLocation(3, 10.823099, 106.629664, "ShopMate Store - District 3", "789 Vo Van Tan, District 3, HCMC"));
        sampleLocations.add(new StoreLocation(4, 10.758733, 106.704450, "ShopMate Store - District 4", "101 Khanh Hoi, District 4, HCMC"));
        sampleLocations.add(new StoreLocation(13, 2, 2, "Store A", "HCM"));
        return sampleLocations;
    }

    public LiveData<List<StoreLocation>> getStoreLocations() {
        return storeLocations;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
} 