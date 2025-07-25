package com.example.shopmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Category;
import com.example.shopmate.data.network.CategoryApi;
import com.example.shopmate.data.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryViewModel extends ViewModel {

    private CategoryApi categoryApi;
    private List<Category> allCategories; // Danh sách đầy đủ từ API
    private List<Category> filteredCategories; // Danh sách sau khi search

    private MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private MutableLiveData<Category> categoryDetail = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> operationSuccess = new MutableLiveData<>();

    public AdminCategoryViewModel() {
        categoryApi = RetrofitClient.getInstance().create(CategoryApi.class);
        allCategories = new ArrayList<>();
        filteredCategories = new ArrayList<>();
    }

    // Getters for LiveData
    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<Category> getCategoryDetail() { return categoryDetail; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getOperationSuccess() { return operationSuccess; }

    public void loadCategories() {
        isLoading.setValue(true);
        errorMessage.setValue("");

        // Gọi API để lấy dữ liệu thực
        categoryApi.getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Category>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Lưu dữ liệu thực từ API
                        allCategories = apiResponse.getData();
                        filteredCategories = new ArrayList<>(allCategories);
                        categories.setValue(filteredCategories);
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to load categories");
                    }
                } else {
                    errorMessage.setValue("Network error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Connection failed: " + t.getMessage());
            }
        });
    }

    // Search theo tên category (không gọi API, tìm trong dữ liệu đã có)
    public void searchCategories(String query) {
        if (allCategories == null || allCategories.isEmpty()) {
            // Nếu chưa có dữ liệu, load lại từ API
            loadCategories();
            return;
        }

        isLoading.setValue(true);
        errorMessage.setValue("");

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            isLoading.setValue(false);

            if (query == null || query.trim().isEmpty()) {
                // Nếu query rỗng, hiển thị tất cả category
                filteredCategories = new ArrayList<>(allCategories);
                categories.setValue(filteredCategories);
                return;
            }

            // Lọc theo tên category trong dữ liệu đã có
            final String searchLower = query.toLowerCase().trim();
            List<Category> result = allCategories.stream()
                .filter(category -> category.getCategoryName() != null &&
                        category.getCategoryName().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());

            filteredCategories = result;

            if (filteredCategories.isEmpty()) {
                errorMessage.setValue("Không tìm thấy danh mục phù hợp");
                categories.setValue(new ArrayList<>());
            } else {
                categories.setValue(filteredCategories);
            }
        }, 100); // Thời gian ngắn hơn vì chỉ filter dữ liệu local
    }

    public void addCategory(Category category) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        categoryApi.createCategory(category).enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Category> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        operationSuccess.setValue(true);
                        // Reload lại danh sách sau khi thêm thành công
                        loadCategories();
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to add category");
                    }
                } else {
                    errorMessage.setValue("Failed to add category: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to add category: " + t.getMessage());
            }
        });
    }

    public void updateCategory(int categoryId, Category category) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        categoryApi.updateCategory(categoryId, category).enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Category> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        operationSuccess.setValue(true);
                        // Reload lại danh sách sau khi update thành công
                        loadCategories();
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to update category");
                    }
                } else {
                    errorMessage.setValue("Failed to update category: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to update category: " + t.getMessage());
            }
        });
    }

    public void deleteCategory(int categoryId) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        categoryApi.deleteCategory(categoryId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        operationSuccess.setValue(true);
                        // Reload lại danh sách sau khi xóa thành công
                        loadCategories();
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to delete category");
                    }
                } else {
                    errorMessage.setValue("Failed to delete category: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to delete category: " + t.getMessage());
            }
        });
    }

    public void getCategoryById(int categoryId) {
        isLoading.setValue(true);
        errorMessage.setValue("");

        categoryApi.getCategoryById(categoryId).enqueue(new Callback<ApiResponse<Category>>() {
            @Override
            public void onResponse(Call<ApiResponse<Category>> call, Response<ApiResponse<Category>> response) {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Category> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        categoryDetail.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue(apiResponse.getMessage() != null ?
                            apiResponse.getMessage() : "Failed to load category detail");
                    }
                } else {
                    // Enhanced error handling
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            android.util.Log.e("AdminCategoryVM", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        android.util.Log.e("AdminCategoryVM", "Error reading error body", e);
                    }

                    String errorMsg = "Failed to load category: " + response.code();
                    if (!errorBody.isEmpty()) {
                        errorMsg += " - " + errorBody;
                    }
                    errorMessage.setValue(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Category>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Failed to load category: " + t.getMessage());
            }
        });
    }

    // Clear flags
    public void clearOperationSuccess() {
        operationSuccess.setValue(false);
    }

    public void clearError() {
        errorMessage.setValue("");
    }
}
