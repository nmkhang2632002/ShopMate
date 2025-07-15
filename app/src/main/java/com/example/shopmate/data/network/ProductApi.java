package com.example.shopmate.data.network;

import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.model.ApiResponse;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

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
}
