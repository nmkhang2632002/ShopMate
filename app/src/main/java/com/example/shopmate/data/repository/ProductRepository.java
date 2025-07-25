package com.example.shopmate.data.repository;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.network.ProductApi;
import com.example.shopmate.data.network.RetrofitClient;

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
        Log.d("ProductRepository", "=== loadProductById called for ID: " + productId + " ===");
        isDetailLoading.setValue(true);
        detailErrorMessage.setValue(null);
        
        productApi.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                isDetailLoading.setValue(false);
                Log.d("ProductRepository", "API Response received for product ID: " + productId);
                Log.d("ProductRepository", "Response successful: " + response.isSuccessful());
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    Log.d("ProductRepository", "API Response successful: " + apiResponse.isSuccessful());
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        Product product = apiResponse.getData();
                        Log.d("ProductRepository", "Product received: " + product.getProductName());
                        Log.d("ProductRepository", "Product image URL: " + product.getImageURL());
                        productDetailLiveData.setValue(product);
                    } else {
                        Log.e("ProductRepository", "API Response not successful: " + apiResponse.getMessage());
                        detailErrorMessage.setValue("Product not found: " + apiResponse.getMessage());
                    }
                } else {
                    Log.e("ProductRepository", "HTTP Response not successful: " + response.code());
                    detailErrorMessage.setValue("Failed to load product: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                isDetailLoading.setValue(false);
                Log.e("ProductRepository", "API call failed: " + t.getMessage());
                detailErrorMessage.setValue("Network error: " + t.getMessage());
            }
        });
    }
}
