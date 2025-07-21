package com.example.shopmate.data.network;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.data.model.CreateOrderRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface OrderApi {
    @POST("orders/{userId}/create")
    Call<ApiResponse<Order>> createOrder(
        @Path("userId") int userId,
        @Body CreateOrderRequest request
    );
}