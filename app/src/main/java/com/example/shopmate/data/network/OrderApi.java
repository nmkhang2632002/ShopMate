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

    @GET("orders/user/{userId}")
    Call<ApiResponse<List<Order>>> getOrdersByUserId(@Path("userId") int userId);

    // Get order by ID - API: GET /orders/{orderId}
    @GET("orders/{orderId}")
    Call<ApiResponse<Order>> getOrderById(@Path("orderId") int orderId);

    // Get order detail - API: GET /orders/detail/{orderId}
    @GET("orders/detail/{orderId}")
    Call<ApiResponse<Order>> getOrderDetail(@Path("orderId") int orderId);

    // Update order status to delivered - API: PUT /orders/cod/{orderId}/delivered
    @PUT("orders/cod/{orderId}/delivered")
    Call<ApiResponse<Order>> updateOrderToDelivered(@Path("orderId") int orderId);

    // Update order status to failed - API: PUT /orders/cod/{orderId}/failed
    @PUT("orders/cod/{orderId}/failed")
    Call<ApiResponse<Order>> updateOrderToFailed(
        @Path("orderId") int orderId,
        @Query("reason") String reason
    );

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

    @PUT("admin/orders/{orderId}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
        @Path("orderId") int orderId,
        @Body UpdateOrderStatusRequest request
    );
}