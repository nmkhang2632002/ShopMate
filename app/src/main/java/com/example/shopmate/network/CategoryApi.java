package com.example.shopmate.network;

import com.example.shopmate.model.ApiResponse;
import com.example.shopmate.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoryApi {
    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();
}
