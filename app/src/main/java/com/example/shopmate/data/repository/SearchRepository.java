package com.example.shopmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.network.ProductApi;
import com.example.shopmate.data.network.RetrofitClient;
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
        
        // Use the simplified search API - just pass the productName as the search query
        // For now, we'll ignore the other parameters since the API was simplified
        String searchQuery = productName != null ? productName : "";

        Call<ApiResponse<List<Product>>> call = productApi.searchProducts(searchQuery);

        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call,
                                 Response<ApiResponse<List<Product>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        List<Product> products = apiResponse.getData();
                        if (products != null) {
                            searchResults.setValue(products);
                        } else {
                            searchResults.setValue(new ArrayList<>());
                        }
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "No products found");
                    }
                } else {
                    errorMessage.setValue("Lỗi kết nối mạng");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
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
        // Since we simplified the API and don't have filter options endpoint,
        // we'll provide default filter options or load all products to extract categories
        Call<ApiResponse<List<Product>>> call = productApi.getProducts();

        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call,
                                 Response<ApiResponse<List<Product>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Create a simple filter options response from available products
                        // This is a workaround since the simplified API doesn't have filter endpoints
                        FilterOptionsResponse filterOptions = new FilterOptionsResponse();
                        // You can set default categories or extract from products
                        SearchRepository.this.filterOptions.setValue(filterOptions);
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to load filter options");
                    }
                } else {
                    errorMessage.setValue("Lỗi tải filter options");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    public void getMostOrderedProducts(int limit) {
        // Since we don't have a most ordered products endpoint in the simplified API,
        // we'll just load all products as a fallback
        isLoading.setValue(true);
        
        Call<ApiResponse<List<Product>>> call = productApi.getProducts();

        call.enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call,
                                 Response<ApiResponse<List<Product>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Convert products to ProductStatsResponse for compatibility
                        // This is a workaround since we don't have the stats endpoint
                        List<ProductStatsResponse> statsResponse = new ArrayList<>();
                        // Add conversion logic here if needed
                        mostOrderedProducts.setValue(statsResponse);
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to load popular products");
                    }
                } else {
                    errorMessage.setValue("Lỗi tải sản phẩm phổ biến");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
