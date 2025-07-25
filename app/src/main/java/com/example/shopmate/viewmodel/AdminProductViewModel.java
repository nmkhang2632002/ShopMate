package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Product;
import com.example.shopmate.data.network.ProductApi;
import com.example.shopmate.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductViewModel extends ViewModel {

    private ProductApi productApi;
    private List<Product> allProducts; // Danh sách đầy đủ từ API
    private List<Product> filteredProducts; // Danh sách sau khi filter

    private MutableLiveData<List<Product>> products = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();
    private MutableLiveData<Product> productDetail = new MutableLiveData<>();
    private MutableLiveData<List<Product>> mostOrderedProducts = new MutableLiveData<>();

    // Biến lưu trạng thái filter và search hiện tại
    private String currentSearchQuery = "";
    private String currentCategoryFilter = "All";

    public AdminProductViewModel() {
        productApi = RetrofitClient.getInstance().create(ProductApi.class);
        allProducts = new ArrayList<>();
        filteredProducts = new ArrayList<>();
    }

    // Getters for LiveData
    public LiveData<List<Product>> getProducts() { return products; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }
    public LiveData<Product> getProductDetail() { return productDetail; }
    public LiveData<List<Product>> getMostOrderedProducts() { return mostOrderedProducts; }

    public void loadProducts() {
        isLoading.setValue(true);
        errorMessage.setValue("");

        // Gọi API để lấy dữ liệu thực
        productApi.getProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Lưu dữ liệu thực từ API
                        allProducts = apiResponse.getData();
                        // Reset filter và hiển thị tất cả sản phẩm
                        currentSearchQuery = "";
                        currentCategoryFilter = "All";
                        filteredProducts = new ArrayList<>(allProducts);
                        products.setValue(filteredProducts);
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to load products");
                    }
                } else {
                    errorMessage.setValue("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Connection failed: " + t.getMessage());
            }
        });
    }

    // Search theo tên sản phẩm (không gọi API, tìm trong dữ liệu đã có)
    public void searchProducts(String query) {
        currentSearchQuery = query != null ? query.trim() : "";
        applyFilters();
    }

    // Filter theo category (không gọi API, tìm trong dữ liệu đã có)
    public void filterByCategory(String categoryName) {
        currentCategoryFilter = categoryName != null ? categoryName : "All";
        applyFilters();
    }

    // Áp dụng cả search và filter trên dữ liệu đã có
    private void applyFilters() {
        if (allProducts == null || allProducts.isEmpty()) {
            // Nếu chưa có dữ liệu, load lại từ API
            loadProducts();
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue("");

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            isLoading.setValue(false);

            List<Product> result = new ArrayList<>(allProducts);

            // Áp dụng filter category trước
            if (!currentCategoryFilter.equals("All") && !currentCategoryFilter.isEmpty()) {
                result = result.stream()
                    .filter(product -> product.getCategoryName() != null &&
                            product.getCategoryName().equalsIgnoreCase(currentCategoryFilter))
                    .collect(Collectors.toList());
            }

            // Sau đó áp dụng search theo tên
            if (!currentSearchQuery.isEmpty()) {
                final String searchLower = currentSearchQuery.toLowerCase();
                result = result.stream()
                    .filter(product -> product.getProductName() != null &&
                            product.getProductName().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
            }

            filteredProducts = result;

            if (filteredProducts.isEmpty()) {
                errorMessage.setValue("Không tìm thấy sản phẩm phù hợp");
                products.setValue(new ArrayList<>());
            } else {
                products.setValue(filteredProducts);
            }
        }, 100); // Thời gian ngắn hơn vì chỉ filter dữ liệu local
    }

    // Reset về trạng thái ban đầu
    public void resetFilters() {
        currentSearchQuery = "";
        currentCategoryFilter = "All";
        if (allProducts != null && !allProducts.isEmpty()) {
            filteredProducts = new ArrayList<>(allProducts);
            products.setValue(filteredProducts);
        } else {
            loadProducts();
        }
    }

    // Các phương thức khác giữ nguyên để không ảnh hưởng đến chức năng CRUD
    public void addProduct(Product product) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        productApi.createProduct(product).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        operationSuccess.setValue(true);
                        // Reload lại danh sách sau khi thêm thành công
                        loadProducts();
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to add product");
                    }
                } else {
                    errorMessage.setValue("Failed to add product: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to add product: " + t.getMessage());
            }
        });
    }

    public void updateProduct(int productId, Product product) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        productApi.updateProduct(productId, product).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        operationSuccess.setValue(true);
                        // Reload lại danh sách sau khi update thành công
                        loadProducts();
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to update product");
                    }
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            android.util.Log.e("AdminProductVM", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("AdminProductVM", "Error reading error body", e);
                    }

                    String errorMsg = "Failed to update product: " + response.code();
                    if (!errorBody.isEmpty()) {
                        errorMsg += " - " + errorBody;
                    }
                    android.util.Log.e("AdminProductVM", errorMsg);
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to update product: " + t.getMessage());
            }
        });
    }

    public void deleteProduct(int productId) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        productApi.deleteProduct(productId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        operationSuccess.setValue(true);
                        // Reload lại danh sách sau khi xóa thành công
                        loadProducts();
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to delete product");
                    }
                } else {
                    errorMessage.setValue("Failed to delete product: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to delete product: " + t.getMessage());
            }
        });
    }

    // Get product detail by ID
    public void getProductDetail(int productId) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        productApi.getProductById(productId).enqueue(new Callback<ApiResponse<Product>>() {
            @Override
            public void onResponse(Call<ApiResponse<Product>> call, Response<ApiResponse<Product>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Product> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        productDetail.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to load product detail");
                    }
                } else {
                    errorMessage.setValue("Failed to load product detail: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Product>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to load product detail: " + t.getMessage());
            }
        });
    }

    // Get products by category
    public void getProductsByCategory(int categoryId) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        productApi.getProductsByCategory(categoryId).enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        products.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "No products found in this category");
                    }
                } else {
                    errorMessage.setValue("Failed to load products by category: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to load products by category: " + t.getMessage());
            }
        });
    }

    // Get most ordered products - simplified version using available endpoints
    public void getMostOrderedProducts(int limit) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        // Since we don't have a specific most ordered products endpoint,
        // we'll load all products and sort by totalOrdered field
        productApi.getProducts().enqueue(new Callback<ApiResponse<List<Product>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Product>>> call, Response<ApiResponse<List<Product>>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Product>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        List<Product> allProducts = apiResponse.getData();

                        // Sort by totalOrdered and take top items
                        allProducts.sort((p1, p2) -> Integer.compare(p2.getTotalOrdered(), p1.getTotalOrdered()));

                        // Limit the results
                        List<Product> topProducts = allProducts.subList(0, Math.min(limit, allProducts.size()));
                        mostOrderedProducts.setValue(topProducts);
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to load product statistics");
                    }
                } else {
                    errorMessage.setValue("Failed to load product statistics: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Product>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to load product statistics: " + t.getMessage());
            }
        });
    }

    // Advanced search with filters - simplified version using available endpoints
    public void advancedSearch(String productName, String category, String priceRange, String sortBy, int page, int size) {
        // Since ProductApi.searchProducts() only accepts one parameter (search query),
        // we'll implement a simplified version using the available endpoints

        if (category != null && !category.isEmpty() && !category.equals("All")) {
            // Use category filtering if specified
            int categoryId = getCategoryIdFromName(category);
            if (categoryId > 0) {
                getProductsByCategory(categoryId);
                return;
            }
        }

        // Fallback to simple search or load all products
        if (productName != null && !productName.trim().isEmpty()) {
            searchProducts(productName);
        } else {
            loadProducts();
        }
    }

    // Filter products with multiple criteria - simplified version
    public void filterProducts(String category, String priceRange, String sortBy) {
        // For now, we'll just implement category filtering using the available endpoint
        if (category != null && !category.isEmpty() && !category.equals("All")) {
            // Try to find category ID - this is a simplified approach
            // In a real app, you'd have a category mapping
            int categoryId = getCategoryIdFromName(category);
            if (categoryId > 0) {
                getProductsByCategory(categoryId);
            } else {
                loadProducts(); // Fallback to all products
            }
        } else {
            loadProducts(); // Load all products if no specific category
        }
    }

    // Helper method to map category names to IDs
    private int getCategoryIdFromName(String categoryName) {
        // This is a simplified mapping - in a real app, you'd get this from API
        switch (categoryName) {
            case "Electronics": return 1;
            case "Clothing": return 2;
            case "Home & Kitchen": return 3;
            case "Beauty & Health": return 4;
            default: return -1;
        }
    }
}

