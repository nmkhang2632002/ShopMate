package com.example.shopmate.data.network;

import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.response.ProductSearchResponse;
import com.example.shopmate.data.response.FilterOptionsResponse;
import com.example.shopmate.data.response.ProductStatsResponse;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApi {
    @GET("products")
    Call<ApiResponse<List<Product>>> getProducts();

    @GET("products/{productId}")
    Call<ApiResponse<Product>> getProductById(
        @Path("productId") int productId
    );

    @GET("products/category/{categoryId}")
    Call<ApiResponse<List<Product>>> getProductsByCategory(
        @Path("categoryId") int categoryId
    );

    // Search API endpoints
    @GET("products/search")
    Call<ApiResponse<ProductSearchResponse>> searchProducts(
        @Query("productName") String productName,
        @Query("category") String category,
        @Query("priceRange") String priceRange,
        @Query("sortBy") String sortBy,
        @Query("page") int page,
        @Query("size") int size
    );

    @GET("products/filter-options")
    Call<ApiResponse<FilterOptionsResponse>> getFilterOptions();

    @GET("products/stats/most-ordered")
    Call<ApiResponse<List<ProductStatsResponse>>> getMostOrderedProducts(
        @Query("limit") int limit
    );
}
