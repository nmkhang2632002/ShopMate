package com.example.shopmate.data.network;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.CreateOrderRequest;
import com.example.shopmate.data.model.OrderDetail;
import com.example.shopmate.data.model.OrderDetailResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VNPayApi {
    
    @POST("vnpay/create-order-and-payment/{userId}")
    Call<ApiResponse<String>> createOrderAndPayment(
            @Path("userId") int userId,
            @Body CreateOrderRequest request,
            @Query("mobileReturnUrl") String mobileReturnUrl
    );

    @GET("vnpay/order-detail/{orderId}")
    Call<ApiResponse<OrderDetailResponse>> getOrderDetail(@Path("orderId") int orderId);
}
