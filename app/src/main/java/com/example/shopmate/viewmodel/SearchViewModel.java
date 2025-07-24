package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.repository.SearchRepository;
import com.example.shopmate.data.response.FilterOptionsResponse;
import com.example.shopmate.data.response.ProductStatsResponse;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private static final String TAG = "SearchViewModel";
    
    private final SearchRepository searchRepository;
    
    private final MediatorLiveData<Boolean> isLoading = new MediatorLiveData<>();
    private final MediatorLiveData<String> error = new MediatorLiveData<>();

    public SearchViewModel() {
        this.searchRepository = new SearchRepository();
        
        // Forward repository data to ViewModel LiveData
        isLoading.addSource(searchRepository.getIsLoading(), isLoading::setValue);
        error.addSource(searchRepository.getErrorMessage(), error::setValue);
    }

    // Getters for LiveData
    public LiveData<List<Product>> getSearchResults() {
        return searchRepository.getSearchResults();
    }

    public LiveData<FilterOptionsResponse> getFilterOptions() {
        return searchRepository.getFilterOptions();
    }

    public LiveData<List<ProductStatsResponse>> getMostOrderedProducts() {
        return searchRepository.getMostOrderedProducts();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    // Get current filter options value (for synchronous access)
    public FilterOptionsResponse getFilterOptionsValue() {
        return searchRepository.getFilterOptions().getValue();
    }

    public void searchProducts(String productName, String category, String priceRange, 
                             String sortBy, int page, int size) {
        searchRepository.searchProducts(productName, category, priceRange, sortBy, page, size);
    }

    public void loadFilterOptions() {
        searchRepository.loadFilterOptions();
    }

    public void getMostOrderedProducts(int limit) {
        searchRepository.getMostOrderedProducts(limit);
    }
}
