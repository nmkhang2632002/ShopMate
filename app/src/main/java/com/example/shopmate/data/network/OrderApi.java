package com.example.shopmate.data.network;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Order;
import com.example.shopmate.data.model.CreateOrderRequest;
import com.example.shopmate.data.model.UpdateOrderStatusRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface OrderApi {
    // User endpoints
    @POST("orders/{userId}/create")
    Call<ApiResponse<Order>> createOrder(
            @Path("userId") int userId,
            @Body CreateOrderRequest request
    );

    @GET("orders/{userId}")
    Call<ApiResponse<List<Order>>> getOrdersByUserId(@Path("userId") int userId);

    // Get order by ID - API: GET /orders/{orderId}
    @GET("orders/{orderId}")
    Call<ApiResponse<Order>> getOrderById(@Path("orderId") int orderId);

    // Get order detail - API: GET /orders/detail/{orderId}
    @GET("orders/detail/{orderId}")
    Call<ApiResponse<Order>> getOrderDetail(@Path("orderId") int orderId);

    // Admin endpoints for order management
    // Get all orders - API: GET /orders (without admin prefix)
    @GET("orders")
    Call<ApiResponse<List<Order>>> getAllOrders(
            @Query("page") int page,
            @Query("size") int size
    );

    // Search orders - API: GET /orders/search
    @GET("orders/search")
    Call<ApiResponse<List<Order>>> searchOrders(
            @Query("query") String query,
            @Query("status") String status,
            @Query("page") int page,
            @Query("size") int size
    );

    // Admin endpoints for order status updates - using SalesApp's actual endpoints
    @PUT("orders/{orderId}/processing")
    Call<ApiResponse<Order>> updateOrderToProcessing(@Path("orderId") int orderId);

    @PUT("orders/{orderId}/delivered") 
    Call<ApiResponse<Order>> updateOrderToDelivered(@Path("orderId") int orderId);

    @PUT("orders/{orderId}/cancelled")
    Call<ApiResponse<Order>> updateOrderToCancelled(@Path("orderId") int orderId);

    @PUT("orders/{orderId}/failed")
    Call<ApiResponse<Order>> updateOrderToFailed(
            @Path("orderId") int orderId,
            @Query("reason") String reason
    );

    // Legacy endpoint with request body (keep for compatibility)
    @PUT("admin/orders/{orderId}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
            @Path("orderId") int orderId,
            @Body UpdateOrderStatusRequest request
    );
}