package com.example.shopmate.data.network;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.*;

public interface CategoryApi {
    // Public endpoints
    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();

    // Get category by ID - API: GET /categories/{id}
    @GET("categories/{id}")
    Call<ApiResponse<Category>> getCategoryById(@Path("id") int categoryId);

    // Admin endpoints for category management
    // POST /admin/categories
    @POST("admin/categories")
    Call<ApiResponse<Category>> createCategory(@Body Category category);

    // PUT /categories/{id}
    @PUT("categories/{id}")
    Call<ApiResponse<Category>> updateCategory(
        @Path("id") int categoryId,
        @Body Category category
    );

    // DELETE /categories/{id}
    @DELETE("categories/{id}")
    Call<ApiResponse<Void>> deleteCategory(@Path("id") int categoryId);

    // Search categories (admin function)
    @GET("admin/categories/search")
    Call<ApiResponse<List<Category>>> searchCategories(
        @Query("query") String query,
        @Query("page") int page,
        @Query("size") int size
    );
}
