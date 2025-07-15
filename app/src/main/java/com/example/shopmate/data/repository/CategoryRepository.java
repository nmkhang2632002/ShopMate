package com.example.shopmate.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.shopmate.data.model.ApiResponse;
import com.example.shopmate.data.model.Category;
import com.example.shopmate.data.network.CategoryApi;
import com.example.shopmate.data.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private final CategoryApi categoryApi;
    private final MutableLiveData<List<Category>> categoriesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public CategoryRepository() {
        categoryApi = RetrofitClient.getInstance().create(CategoryApi.class);
    }

    public LiveData<List<Category>> getCategories() {
        loadCategories();
        return categoriesLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    private void loadCategories() {
        isLoading.setValue(true);
        
        categoryApi.getCategories().enqueue(new Callback<ApiResponse<List<Category>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Category>>> call, Response<ApiResponse<List<Category>>> response) {
                isLoading.setValue(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Category>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.getData() != null) {
                        categoriesLiveData.setValue(apiResponse.getData());
                    } else {
                        errorMessage.setValue("API Error: " + apiResponse.getMessage());
                    }
                } else {
                    errorMessage.setValue("Network Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Category>>> call, Throwable t) {
                isLoading.setValue(false);
                errorMessage.setValue("Network Error: " + t.getMessage());
            }
        });
    }
}
