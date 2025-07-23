package com.example.shopmate.data.network;

import com.example.shopmate.data.model.Cart;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.AddToCartRequest;
import com.example.shopmate.data.model.UpdateCartItemQuantityRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApi {
    @GET("carts/{userId}")
    Call<ApiResponse<Cart>> getCart(@Path("userId") int userId);
    
    @POST("carts/{userId}/add")
    Call<ApiResponse<Cart>> addToCart(@Path("userId") int userId, @Body AddToCartRequest request);

    @PUT("carts/{userId}/update")
    Call<ApiResponse<Cart>> updateCartItemQuantity(@Path("userId") int userId, @Body UpdateCartItemQuantityRequest request);

    @DELETE("carts/{userId}/remove/{cartItemId}")
    Call<ApiResponse<Cart>> removeCartItem(@Path("userId") int userId, @Path("cartItemId") int cartItemId);

    @DELETE("carts/{userId}/clear")
    Call<ApiResponse<Cart>> clearCart(@Path("userId") int userId);
}