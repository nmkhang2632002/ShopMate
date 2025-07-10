package com.example.shopmate.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.model.ApiResponse;
import com.example.shopmate.model.Product;
import com.example.shopmate.network.ProductApi;
import com.example.shopmate.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRepository {
    private final ProductApi productApi;
    private final MutableLiveData<List<Product>> productsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Product>> categoryProductsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Product> productDetailLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDetailLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> detailErrorMessage = new MutableLiveData<>();
    
    public ProductRepository() {
        productApi = RetrofitClient.getInstance().create(ProductApi.class);
    }

    public LiveData<List<Product>> getProducts() {
        loadProducts();
        return productsLiveData;
    }

    public LiveData<List<Product>> getProductsByCategory(int categoryId) {
        loadProductsByCategory(categoryId);
        return categoryProductsLiveData;
    }

    public LiveData<Product> getProductById(int productId) {
        loadProductById(productId);
        return productDetailLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsDetailLoading() {
        return isDetailLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getDetailErrorMessage() {
        return detailErrorMessage;
    }

    private void loadProducts() {
        isLoading.setValue(true);
        
        productApi.getProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        productsLiveData.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    errorMessage.setValue("Network Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network Error: " + t.getMessage());
            }
        });
    }

    private void loadProductsByCategory(int categoryId) {
        isLoading.setValue(true);
        
        productApi.getProductsByCategory(categoryId).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        categoryProductsLiveData.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    errorMessage.setValue("Network Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network Error: " + t.getMessage());
            }
        });
    }

    private void loadProductById(int productId) {
        isDetailLoading.setValue(true);
        detailErrorMessage.setValue(null);
        
        productApi.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                isDetailLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        productDetailLiveData.setValue(apiResponse.getData());
                    } else {
                        detailErrorMessage.setValue("Product not found: " + apiResponse.getMessage());
                    }
                } else {
                    detailErrorMessage.setValue("Failed to load product: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                isDetailLoading.setValue(false);
                detailErrorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
