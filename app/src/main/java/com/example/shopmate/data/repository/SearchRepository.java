package com.example.shopmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.network.ProductApi;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.data.response.ProductSearchResponse;
import com.example.shopmate.data.response.FilterOptionsResponse;
import com.example.shopmate.data.response.ProductStatsResponse;
import com.example.shopmate.data.model.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchRepository {
    private final ProductApi productApi;
    
    private final MutableLiveData<List<Product>> searchResults = new MutableLiveData<>();
    private final MutableLiveData<FilterOptionsResponse> filterOptions = new MutableLiveData<>();
    private final MutableLiveData<List<ProductStatsResponse>> mostOrderedProducts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public SearchRepository() {
        this.productApi = RetrofitClient.getInstance().create(ProductApi.class);
    }

    public LiveData<List<Product>> getSearchResults() {
        return searchResults;
    }

    public LiveData<FilterOptionsResponse> getFilterOptions() {
        return filterOptions;
    }

    public LiveData<List<ProductStatsResponse>> getMostOrderedProducts() {
        return mostOrderedProducts;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void searchProducts(String productName, String category, String priceRange, 
                             String sortBy, int page, int size) {
        isLoading.setValue(true);
        
        Call<ApiResponse<ProductSearchResponse>> call = productApi.searchProducts(
            productName, category, priceRange, sortBy, page, size
        );
        
        call.enqueue(new Callback<ApiResponse<ProductSearchResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductSearchResponse>> call, 
                                 Response<ApiResponse<ProductSearchResponse>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductSearchResponse> apiResponse = response.body();
                    if (apiResponse.isSuccessful()) {
                        ProductSearchResponse searchResponse = apiResponse.getData();
                        if (searchResponse != null && searchResponse.getContent() != null) {
                            searchResults.setValue(searchResponse.getContent());
                        } else {
                            searchResults.setValue(new ArrayList<>());
                        }
                    } else {
                        errorMessage.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorMessage.setValue("Lỗi kết nối mạng");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductSearchResponse>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Add a simplified search method for easier use
    public void searchProducts(String query) {
        searchProducts(query, null, null, null, 0, 20);
    }

    public void loadFilterOptions() {
        Call<ApiResponse<FilterOptionsResponse>> call = productApi.getFilterOptions();
        
        call.enqueue(new Callback<ApiResponse<FilterOptionsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<FilterOptionsResponse>> call, 
                                 Response<ApiResponse<FilterOptionsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<FilterOptionsResponse> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        filterOptions.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorMessage.setValue("Lỗi tải filter options");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FilterOptionsResponse>> call, Throwable t) {
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getMostOrderedProducts(int limit) {
        isLoading.setValue(true);
        
        Call<ApiResponse<List<ProductStatsResponse>>> call = productApi.getMostOrderedProducts(limit);
        
        call.enqueue(new Callback<ApiResponse<List<ProductStatsResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ProductStatsResponse>>> call, 
                                 Response<ApiResponse<List<ProductStatsResponse>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<ProductStatsResponse>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        mostOrderedProducts.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue(apiResponse.getMessage());
                    }
                } else {
                    errorMessage.setValue("Lỗi tải sản phẩm phổ biến");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<ProductStatsResponse>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
