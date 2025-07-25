package com.example.shopmate.data.network;

import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.response.ProductSearchResponse;
import com.example.shopmate.data.response.FilterOptionsResponse;
import com.example.shopmate.data.response.ProductStatsResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductApi {
    // Get all products
    @GET("products")
    Call<ApiResponse<List<Product>>> getProducts();

    // Get product by ID (for view product functionality)
    @GET("products/{id}")
    Call<ApiResponse<Product>> getProductById(@Path("id") int productId);

    // Get products by category
    @GET("products/category/{categoryId}")
    Call<ApiResponse<List<Product>>> getProductsByCategory(@Path("categoryId") int categoryId);

    // Search products
    @GET("products")
    Call<ApiResponse<List<Product>>> searchProducts(@Query("search") String query);

    // Search products with full parameters (for compatibility)
    @GET("products/search")
    Call<ApiResponse<ProductSearchResponse>> searchProducts(
        @Query("productName") String productName,
        @Query("category") String category,
        @Query("priceRange") String priceRange,
        @Query("sortBy") String sortBy,
        @Query("page") int page,
        @Query("size") int size
    );

    // Admin endpoints - Create new product
    @POST("products")
    Call<ApiResponse<Product>> createProduct(@Body Product product);

    @PUT("products/{id}")
    Call<ApiResponse<Product>> updateProduct(
            @Path("id") int productId,
            @Body Product product
    );

    @DELETE("products/{id}")
    Call<ApiResponse<Void>> deleteProduct(@Path("id") int productId);

    // Admin endpoints - Search products with pagination
    @GET("products/search")
    Call<ApiResponse<List<Product>>> searchProductsAdmin(
            @Query("query") String query,
            @Query("page") int page,
            @Query("size") int size
    );

    // Upload product image
    @Multipart
    @POST("products/{productId}/upload-image")
    Call<ApiResponse<String>> uploadProductImage(
            @Path("productId") int productId,
            @Part MultipartBody.Part image
    );

    @GET("products/filter-options")
    Call<ApiResponse<FilterOptionsResponse>> getFilterOptions();

    @GET("products/stats/most-ordered")
    Call<ApiResponse<List<ProductStatsResponse>>> getMostOrderedProducts(
            @Query("limit") int limit
    );
}