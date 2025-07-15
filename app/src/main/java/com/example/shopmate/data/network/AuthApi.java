package com.example.shopmate.data.network;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.LoginRequest;
import com.example.shopmate.data.model.LoginResponse;
import com.example.shopmate.data.model.LogoutRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);
    
    @POST("auth/logout")
    Call<ApiResponse<Void>> logout(@Body LogoutRequest request);
} 