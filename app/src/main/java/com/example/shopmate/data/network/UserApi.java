package com.example.shopmate.data.network;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface UserApi {
    
    @GET("users/{userId}")
    Call<ApiResponse<User>> getUserById(@Path("userId") int userId);
}
