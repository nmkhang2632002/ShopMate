package com.example.shopmate.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.model.Cart;
import com.example.shopmate.model.ApiResponse;
import com.example.shopmate.model.AddToCartRequest;
import com.example.shopmate.network.CartApi;
import com.example.shopmate.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartRepository {
    private static CartRepository instance;
    private final CartApi cartApi;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    
    private CartRepository() {
        cartApi = RetrofitClient.getInstance().create(CartApi.class);
    }
    
    public static synchronized CartRepository getInstance() {
        if (instance == null) {
            instance = new CartRepository();
        }
        return instance;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }
    
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
    
    public LiveData<Cart> getCart(int userId) {
        MutableLiveData<Cart> cartData = new MutableLiveData<>();
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        cartApi.getCart(userId).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Cart> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        cartData.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue("API Error: " + apiResponse.getMessage());
                        cartData.setValue(null);
                    }
                } else {
                    errorMessage.setValue("Network Error: " + response.code());
                    cartData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network Error: " + t.getMessage());
                cartData.setValue(null);
            }

        });
        
        return cartData;
    }
    
    public LiveData<Cart> addToCart(int userId, int productId, int quantity, double price) {
        MutableLiveData<Cart> cartData = new MutableLiveData<>();
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        AddToCartRequest request = new AddToCartRequest(productId, quantity, price);
        
        cartApi.addToCart(userId, request).enqueue(new Callback<ApiResponse<Cart>>() {
            @Override
            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Cart> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        cartData.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue("Failed to add to cart: " + apiResponse.getMessage());
                        cartData.setValue(null);
                    }
                } else {
                    errorMessage.setValue("Failed to add to cart: " + response.code());
                    cartData.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to add to cart: " + t.getMessage());
                cartData.setValue(null);
            }
        });
        
        return cartData;
    }
    
    public LiveData<Cart> updateCartItemQuantity(int userId, int itemId, int quantity) {
        MutableLiveData<Cart> cartData = new MutableLiveData<>();

//        isLoading.setValue(true);
//        errorMessage.setValue(null);
//        
//        cartApi.updateCartItemQuantity(userId, itemId, quantity).enqueue(new Callback<ApiResponse<Cart>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
//                isLoading.setValue(false);
//                
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<Cart> apiResponse = response.body();
//                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
//                        cartData.setValue(apiResponse.getData());
//                    } else {
//                        errorMessage.setValue("Failed to update quantity: " + apiResponse.getMessage());
//                        cartData.setValue(null);
//                    }
//                } else {
//                    errorMessage.setValue("Failed to update quantity: " + response.code());
//                    cartData.setValue(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
//                isLoading.setValue(false);
//                errorMessage.setValue("Failed to update quantity: " + t.getMessage());
//                cartData.setValue(null);
//            }
//        });

        return cartData;
    }
    
    public LiveData<Cart> removeCartItem(int userId, int itemId) {
        MutableLiveData<Cart> cartData = new MutableLiveData<>();
        
//        isLoading.setValue(true);
//        errorMessage.setValue(null);
//        
//        cartApi.removeCartItem(userId, itemId).enqueue(new Callback<ApiResponse<Cart>>() {
//            @Override
//            public void onResponse(Call<ApiResponse<Cart>> call, Response<ApiResponse<Cart>> response) {
//                isLoading.setValue(false);
//                
//                if (response.isSuccessful() && response.body() != null) {
//                    ApiResponse<Cart> apiResponse = response.body();
//                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
//                        cartData.setValue(apiResponse.getData());
//                    } else {
//                        errorMessage.setValue("Failed to remove item: " + apiResponse.getMessage());
//                        cartData.setValue(null);
//                    }
//                } else {
//                    errorMessage.setValue("Failed to remove item: " + response.code());
//                    cartData.setValue(null);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ApiResponse<Cart>> call, Throwable t) {
//                isLoading.setValue(false);
//                errorMessage.setValue("Failed to remove item: " + t.getMessage());
//                cartData.setValue(null);
//            }
//        });

        return cartData;
    }
} 