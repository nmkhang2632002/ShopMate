package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.StoreLocation;
import com.example.shopmate.data.repository.StoreRepository;

import java.util.List;

public class MapViewModel extends ViewModel {
    private final StoreRepository storeRepository;
    
    public MapViewModel() {
        storeRepository = new StoreRepository();
        loadStoreLocations();
    }
    
    public void loadStoreLocations() {
        storeRepository.fetchStoreLocations();
    }
    
    public LiveData<List<StoreLocation>> getStoreLocations() {
        return storeRepository.getStoreLocations();
    }
    
    public LiveData<Boolean> getIsLoading() {
        return storeRepository.getIsLoading();
    }
    
    public LiveData<String> getErrorMessage() {
        return storeRepository.getErrorMessage();
    }
} 