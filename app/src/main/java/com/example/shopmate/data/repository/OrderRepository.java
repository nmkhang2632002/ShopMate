package com.example.shopmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.CreateOrderRequest;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.data.network.OrderApi;
import com.example.shopmate.data.network.VNPayApi;
import com.example.shopmate.data.network.RetrofitClient;
import com.example.shopmate.config.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRepository {

    private final OrderApi orderApi;
    private final VNPayApi vnPayApi;
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public OrderRepository() {
        orderApi = RetrofitClient.getInstance().create(OrderApi.class);
        vnPayApi = RetrofitClient.getInstance().create(VNPayApi.class);
    }
    
    public LiveData<Order> createOrder(int userId, String paymentMethod, String billingAddress) {
        MutableLiveData<Order> orderData = new MutableLiveData<>();
        
        isLoading.setValue(true);
        errorMessage.setValue(null);
        
        CreateOrderRequest request = new CreateOrderRequest(paymentMethod, billingAddress);
        
        orderApi.createOrder(userId, request).enqueue(new Callback<ApiResponse<Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<Order>> call, Response<ApiResponse<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Order> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        // After creating order successfully, fetch the latest order to get complete data
                        fetchLatestOrderForUser(userId, orderData);
                    } else {
                        isLoading.setValue(false);
                        errorMessage.setValue("Failed to create order: " + apiResponse.getMessage());
                    }
                } else {
                    isLoading.setValue(false);
                    errorMessage.setValue("Network error: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Order>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });
        
        return orderData;
    }

    public LiveData<String> createVNPayOrder(int userId, String billingAddress) {
        MutableLiveData<String> vnpayUrlData = new MutableLiveData<>();

        isLoading.setValue(true);
        errorMessage.setValue(null);

        CreateOrderRequest request = new CreateOrderRequest("VNPay", billingAddress);
        String mobileReturnUrl = Constants.MOBILE_RETURN_URL;

        vnPayApi.createOrderAndPayment(userId, request, mobileReturnUrl).enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        vnpayUrlData.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue("Failed to create VNPay order: " + apiResponse.getMessage());
                    }
                } else {
                    errorMessage.setValue("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error: " + t.getMessage());
            }
        });

        return vnpayUrlData;
    }
    
    private void fetchLatestOrderForUser(int userId, MutableLiveData<Order> orderData) {
        // Fetch user orders to get the latest one with complete data
        orderApi.getOrdersByUserId(userId).enqueue(new Callback<ApiResponse<List<Order>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Order>>> call, Response<ApiResponse<List<Order>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Order>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null && !apiResponse.getData().isEmpty()) {
                        // Get the first order (most recent) as orders are usually sorted by date desc
                        Order latestOrder = apiResponse.getData().get(0);
                        orderData.setValue(latestOrder);
                    } else {
                        errorMessage.setValue("Failed to fetch order details");
                    }
                } else {
                    errorMessage.setValue("Failed to fetch order details: " + response.code());
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<List<Order>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network error while fetching order: " + t.getMessage());
            }
        });
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }
}